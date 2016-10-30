package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.api.FacebookAPI;
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

public class Overview extends Base {


    @BindView(R.id.overview_coord_layout)  CoordinatorLayout coordinatorLayout;
    @BindView(R.id.bottomsheet)            View         bottomsheet;
    @BindView(R.id.login_button)           LoginButton  fbLoginButton;
    @BindView(R.id.CurrentTasksList)       RecyclerView CurrentTasksList;
    @BindView(R.id.btnOverview)            Button       btnOverview;
    @BindView(R.id.btnQuickAddTask)        Button       btnQuickAddTask;
    @BindView(R.id.CheckinButton)          Button       CheckinButton;
    @BindView(R.id.CheckoutButton)         Button       CheckoutButton;
    @BindView(R.id.btnAddTomorrowsPlanning)Button       btnAddTomorrowsPlanning;
    @BindView(R.id.btnDontAddTomorrowsPlanning) Button btnDontAddTomorrowsPlanning;

    FacebookAPI api;
    //CallbackManager callbackManager;

    TaskPresenter       presenter;
    AttendancePresenter attendancePresenter;

    Context     context;
    List<Task>  tasks;
    AlertDialog dialog;
    TaskAdapter adapter;

    @Override
    Activities getCurrentActivity() {
        return Activities.OVERVIEW;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new TaskPresenterImpl(context);
        attendancePresenter = new AttendancePresenterImpl(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // FIXME background location retrieval!
        dialog = builder
                .setView(R.layout.dialog_loading)
                .setTitle("Wachten op locatie.")
                .setMessage("duurt ongeveer 20 seconden of minder.")
                .create();

        // TODO: 17-4-2016 backgroundServicing. - Implementation will need callbacks.
        //AsyncTask databaseloader = new AsyncTask() {
        //    @Override
        //    protected Object doInBackground(Object[] params) {
        tasks = presenter.viewTasks();
        //}
        //};
        //databaseloader.execute(null);

        ArrayList<Task> veryInefficientBackingList = new ArrayList<>(); // FIXME deadlines and filtering.
        if (tasks.size() > 0)
            for (Task task : tasks)
                veryInefficientBackingList.add(task);

        adapter = new TaskAdapter(tasks, this); // important temporary workaround for above issue.

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        CurrentTasksList.setLayoutManager(layoutManager);
        CurrentTasksList.setAdapter(adapter);
        CurrentTasksList.setOnLongClickListener(v -> false);

        api = new FacebookAPI(this);
        api.initializeLoginButton(fbLoginButton, api.getReadProfileCallback(), api.getPermissions());

        btnOverview.setOnClickListener(v1 -> startActivity(new Intent(this, TaskActivity.class)));
        btnQuickAddTask.setOnClickListener(v -> {
            View dialogview = LayoutInflater.from(this).inflate(R.layout.dialog_quickadd_task, null);
            AlertDialog.Builder taskdialogbuilder = new AlertDialog.Builder(this)
                .setTitle("Taak toevoegen")
                .setView(dialogview)
                .setNegativeButton("annuleren", null)
                .setPositiveButton("toevoegen", (dialog1, which) -> {
                    Task added = new Task();
                    added.createdOn = new Date();
                    added.description = ((EditText)dialogview.findViewById(R.id.dialog_quickadd_task_description)).getText().toString();
                    adapter.addItem(adapter.getItemCount(), added);
                    presenter.addTask(added);
                    // todo refresh list automatically
                });
            AlertDialog taskdialog = taskdialogbuilder.create();
            taskdialog.show();
        });

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomsheet);
        sheetBehavior.setPeekHeight(200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(() -> {
            api.callbackManager.onActivityResult(requestCode, resultCode, data);
        }).start();
    }

    @OnClick(R.id.CheckinButton) void checkIn(){
        dialog.show();
        Timestamp checkin = new Timestamp(Timestamp.ARRIVAL);
        attendancePresenter.checkIn(
                checkin, () -> {
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
                    Log.e("Attendance", "Something went wrong. OnError called for location services.");
                });
     }

     @OnClick(R.id.CheckoutButton) void checkOut(View v){
         Timestamp         checkout = new Timestamp(Timestamp.DEPARTURE);
         String timestampstr = "Uitgecheckt om " + checkout.hours + ":" + checkout.minutes + ".";
         Snackbar.make(v, timestampstr, Snackbar.LENGTH_SHORT).show();
         attendancePresenter.checkOut(checkout);
     }
}
