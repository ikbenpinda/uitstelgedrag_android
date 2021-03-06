package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.ui.adapters.AttendanceAdapter;
import achan.nl.uitstelgedrag.ui.presenters.AttendancePresenter;
import achan.nl.uitstelgedrag.ui.presenters.AttendancePresenterImpl;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class    Attendance extends Base {

    AttendanceAdapter       adapter;
    List<Timestamp>         timestamps;
    AlertDialog dialog;
    Context                 context;
    AttendancePresenter     presenter;

    @BindView(R.id.attendances_lv) RecyclerView lv;
    @BindView(R.id.CheckinButton)  Button       CheckinButton;
    @BindView(R.id.CheckoutButton) Button       CheckoutButton;

    @Override
    Activities getCurrentActivity() {
        return Activities.ATTENDANCELOG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new AttendancePresenterImpl(context);
        timestamps = presenter.viewAttendance(); // FIXME

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder
                .setView(R.layout.dialog_loading)
                .setTitle("Wachten op locatie.")
                .setMessage("duurt ongeveer 20 seconden of minder.")
                .create();


        adapter = new AttendanceAdapter(timestamps, this);
        LinearLayoutManager manager    = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        lv.setLayoutManager(manager);
        lv.setAdapter(adapter);
        lv.setOnClickListener(v -> {
            //
        });
    }

    @OnClick(R.id.CheckinButton) void checkIn() {
        dialog.show();
        Timestamp checkin = new Timestamp(Timestamp.ARRIVAL);
        presenter.checkIn(
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
                    Log.e("Attendance", "Something went wrong. OnError called for location services.");
                });

        adapter.addItem(adapter.getItemCount(), checkin);
    }

    @OnClick(R.id.CheckoutButton) void checkOut(View v){
        Timestamp         checkout = new Timestamp(Timestamp.DEPARTURE);
        String timestampstr = "Uitgecheckt om " + checkout.hours + ":" + checkout.minutes + ".";
        Snackbar.make(v, timestampstr, Snackbar.LENGTH_SHORT).show();
        presenter.checkOut(checkout);
        adapter.addItem(adapter.getItemCount(), checkout);
    }


}
