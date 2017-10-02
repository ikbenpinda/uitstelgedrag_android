package achan.nl.uitstelgedrag.api;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import achan.nl.uitstelgedrag.BuildConfig;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Update;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class that handles all backend implementation details.
 *
 * Created by Etienne on 17-7-2016.
 */
public class Backend {

    final int EXPONENTIAL_INTERVAL    = 5000;
    final int MAX_EXPONENTIAL_BACKOFF = 320000;
    final int LINEAR_INTERVAL         = 250;
    final int MAX_LINEAR_INTERVAL     = 16000;

    int exponential_backoff  = EXPONENTIAL_INTERVAL;
    int linear_backoff = LINEAR_INTERVAL;

    UitstelgedragAPI api;
    Context context;
    String BASE_URL = api.BASE_URL.replace("{host}", "192.168.200.19"); // todo remove once ported to a-chan.nl
    Retrofit retrofit;

    public Backend(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public List<Task> synchronizeTasks(List<Task> tasks){

        List<Task> remotetasks = null;

        try {
            remotetasks = api.getTasks().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        remotetasks = null == remotetasks || remotetasks.isEmpty()? Collections.emptyList() : remotetasks;

        List<Task> fromDevice = new ArrayList<>(); // Buffer list of tasks saved only on the device.
        List<Task> fromServer = new ArrayList<>(); // Buffer list of tasks saved only on the server.

        // New from remote.
        for (Task task : remotetasks)
            if (!tasks.contains(task))
                fromServer.add(task);

        tasks.addAll(fromServer);

        // New from device.
        for (Task task : tasks)
            if (!remotetasks.contains(task))
                fromDevice.add(task);

        api.postTasks(fromDevice);

        return tasks;
    }

//    public List<Timestamp> synchronizeAttendances(List<Timestamp> timestamps){
//        // check with server which are unsynchronized and which aren't.
//        // send existing/retrieve new.
//        return newlist; // TODO: 18-7-2016
//    }

    /**
     * https://dev.twitter.com/streaming/overview/connecting
     *
     * @throws RuntimeException
     */
    public void checkForUpdate() throws RuntimeException {

        // If connection could not be established, reset values and throw exception.
        if (exponential_backoff > MAX_EXPONENTIAL_BACKOFF || linear_backoff > MAX_LINEAR_INTERVAL) {

            exponential_backoff   = EXPONENTIAL_INTERVAL;
            linear_backoff        = LINEAR_INTERVAL;

            throw new RuntimeException("Unable to connect!"); // Failed to connect.
        }

        api.getUpdateInfo().enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {

                // Update or server error.

                // Server error.
                // Retry with an interval that doubles on each failure to reduce server load.
                if (!response.isSuccessful())
                    try {
                        Thread.sleep(exponential_backoff);
                        exponential_backoff *= 2; // 5, 10, 20..
                        checkForUpdate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        return;
                    }

                // Update.
                Update update = response.body();
                if (update.version > BuildConfig.VERSION_CODE)
                    ; //update available
                else
                    ; //up-to-date! :D
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {

                // TCP/IP error.
                // This should only be happening after device state has been checked.

                try {
                    Thread.sleep(linear_backoff);
                    // Linear backoff.
                    // Keep retrying, increasing the interval with 250ms/attempt until connection is reestablished,
                    // or unable to connect.
                    linear_backoff += LINEAR_INTERVAL;
                    checkForUpdate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}