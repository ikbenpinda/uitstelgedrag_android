package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.ui.adapters.AttendanceAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

public class AttendanceActivity extends BaseActivity
                                implements OnLocationUpdatedListener, OnReverseGeocodingListener {

    Location location = null;
    Address address = null;

    AttendanceAdapter       adapter;
    UitstelgedragOpenHelper database;
    List<Timestamp>         timestamps;
    AlertDialog             dialog;
    Context                 context;

    @BindView(R.id.attendances_lv) RecyclerView lv;
    @BindView(R.id.CheckinButton)  Button       CheckinButton;
    @BindView(R.id.CheckoutButton) Button       CheckoutButton;

    @Override
    int getLayoutResource() {
        return R.layout.activity_attendance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();

        database = new UitstelgedragOpenHelper(this, null);
        timestamps = database.getTimestamps();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder
                .setView(R.layout.alertdialog_loading)
                .setTitle("Wachten op locatie.")
                .setMessage("duurt ongeveer 20 seconden of minder.")
                .create();


        adapter = new AttendanceAdapter(timestamps, this);
        LinearLayoutManager manager    = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        lv.setLayoutManager(manager);
        lv.setAdapter(adapter);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }


    @OnClick(R.id.CheckinButton) void checkIn(){
        dialog.show();
        getLocation();
    }

    @OnClick(R.id.CheckoutButton) void checkOut(View v){
        Timestamp         checkout = new Timestamp(Timestamp.DEPARTURE);
        String timestampstr = "Uitgecheckt om " + checkout.hours + ":" + checkout.minutes + ".";
        Snackbar.make(v, timestampstr, Snackbar.LENGTH_SHORT).show();
        database.addTimestamp(Timestamp.DEPARTURE);
        adapter.addItem(adapter.getItemCount(), checkout);
    }

    @Override
    public void onLocationUpdated(Location location) {
        this.location = location;
        getAddress();
    }

    public void getLocation(){
        // TODO: 10-5-2016 UI feedback - waiting for location

        // Get GPS coordinates.
        SmartLocation.with(context).
                location()
                //.oneFix() // github issue > apparently broken?
                .start(this);
    }

    public void getAddress(){
        // Translate GPS coordinates to street name.
        SmartLocation.with(context)
                .geocoding()
                .reverse(location, this);
    }


    /**
     * Gets the address for the GPS coordinates and checks in as well.
     * @param location
     * @param list
     */
    @Override
    public void onAddressResolved(Location location, List<Address> list) {
        // TODO: 10-5-2016 NO_NETWORK notification for geocoding!
        // TODO: 10-5-2016 UI feedback - No longer waiting for feedback
        // TODO: 10-5-2016 Move to onCreate()!
        String coordinates = this.location.getLongitude() + "/" + this.location.getLatitude();
        Log.w("LOCATIONSERVICE", "long/lat=" + coordinates);

        dialog.hide();

        String position = "...";

        this.address = (list.isEmpty())? null: list.get(0);

        position = (address != null)? address.getPostalCode():
                coordinates;

        Timestamp checkin = new Timestamp(Timestamp.ARRIVAL);
        String timestampstr = "Ingecheckt om " + checkin.hours + ":" + checkin.minutes + " in " + position;
        Snackbar.make(
                findViewById(android.R.id.content),
                timestampstr,
                Snackbar.LENGTH_SHORT
        ).show();
        database.addTimestamp(Timestamp.ARRIVAL);
        adapter.addItem(adapter.getItemCount(), checkin);
    }

}
