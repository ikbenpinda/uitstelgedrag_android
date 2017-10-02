package achan.nl.uitstelgedrag.api;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Update;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * API definition. Currently no user or user authentication is used,
 * so the API does not ask for authentication yet.
 *
 * Created by Etienne on 17-7-2016.
 */
public interface UitstelgedragAPI {

    /**
     * Base URL of the server running the web service.
     * FIXME Currently the API runs on a local server, so the ip-addresses changes per location; Port ASP to PHP and move it to a-chan.nl.
     */
    String BASE_URL = "https://{host}/api/";

    @GET("tasks")
    Call<List<Task>> getTasks();

    @POST("tasks")
    Call<List<Task>> postTasks(@Body List<Task> fromDevice);

//    @GET("attendances")
//    Call<List<Timestamp>> getAttendances();
//
//    @POST("attendances")
//    Call<List<Timestamp>> postAttendances();

//    @GET("dailies")
//    Call<List<Task>> getDailyObjectives();
//
//    @POST("dailies")
//    Call<List<Task>> postDailyObjectives();
//
//    @GET("moods")
//    Call<List<Task>> getMoodboard();
//
//    @POST("moods")
//    Call<List<Task>> postMoodboard();

    @GET("updates")
    Call<Update> getUpdateInfo();

}
