package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import achan.nl.uitstelgedrag.ui.presenters.AttendancePresenter;
import achan.nl.uitstelgedrag.ui.presenters.AttendancePresenterImpl;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenterImpl;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OverviewActivity extends BaseActivity {

    TaskPresenter presenter;
    AttendancePresenter attendancePresenter;

    Context context;
    List<Task> tasks;
    AlertDialog dialog;
    TaskAdapter adapter;

    @BindView(R.id.ShowAttendancesLogButton) Button       logButton;
    @BindView(R.id.AddTaskButton)            Button       AddTaskButton;
    @BindView(R.id.CheckinButton)            Button       CheckinButton;
    @BindView(R.id.CheckoutButton)           Button       CheckoutButton;
    @BindView(R.id.MainList)                 RecyclerView list;

    @Override
    int getLayoutResource() {
        return R.layout.activity_overview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new TaskPresenterImpl(context);
        attendancePresenter = new AttendancePresenterImpl(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder
                .setView(R.layout.alertdialog_loading)
                .setTitle("Wachten op locatie.")
                .setMessage("duurt ongeveer 20 seconden of minder.")
                .create();

        // TODO: 17-4-2016 backgroundServicing.
        //AsyncTask databaseloader = new AsyncTask() {
        //    @Override
        //    protected Object doInBackground(Object[] params) {
                tasks = presenter.viewTasks();
            //}
        //};
        //databaseloader.execute(null);

        adapter = new TaskAdapter(tasks, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        list.setOnLongClickListener(v -> false);
    }

    @OnClick(R.id.ShowAttendancesLogButton) void submit() {
        Intent intent = new Intent(getBaseContext(), AttendanceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.AddTaskButton) void addTask(View v){
        //EditText cat = (EditText) findViewById(R.id.AddTaskCategory); // TODO: 29-4-2016
        EditText desc = (EditText) findViewById(R.id.AddTaskDescription);
        Task     task = new Task(desc.getText().toString());
        presenter.addTask(task);
        adapter.addItem(adapter.getItemCount(), task);
        //adapter.notifyDataSetChanged();
        Log.i("Uitstelgedrag", "Persisted task #"+task.id);
        desc.setText("");
        desc.clearFocus();
        dismissKeyboard();
    }

    @OnClick(R.id.CheckinButton) void checkIn(){
        dialog.show();
        Timestamp checkin = new Timestamp(Timestamp.ARRIVAL);
        attendancePresenter.checkIn(
                checkin,
                () -> {
                    dialog.hide();
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            "Ingecheckt om " + checkin.hours + ":" + checkin.minutes + " in " + checkin.location,
                            Snackbar.LENGTH_SHORT
                    ).show();
                }, () -> {
                    dialog.hide();
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            "Inchecken mislukt.",
                            Snackbar.LENGTH_SHORT
                    ).show();
                    Log.e("AttendanceActivity", "Something went wrong. OnError called for location services.");
                });

    }

    @OnClick(R.id.CheckoutButton) void checkOut(View v){
        Timestamp         checkout = new Timestamp(Timestamp.DEPARTURE);
        String timestampstr = "Uitgecheckt om " + checkout.hours + ":" + checkout.minutes + ".";
        Snackbar.make(v, timestampstr, Snackbar.LENGTH_SHORT).show();
        attendancePresenter.checkOut(checkout);
    }
}
