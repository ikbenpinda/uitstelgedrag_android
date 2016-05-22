package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Task;
import achan.nl.uitstelgedrag.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

public class OverviewActivity extends AppCompatActivity
                              implements OnLocationUpdatedListener, OnReverseGeocodingListener {

    Context context;
    List<Task> tasks;
    Location location = null;
    Address address = null;
    AlertDialog dialog;
    TaskAdapter adapter;
    String[] draweritems;

    public UitstelgedragOpenHelper databaseHelper;

    @BindView(R.id.ShowAttendancesLogButton)Button       logButton;
    @BindView(R.id.AddTaskButton)           Button       AddTaskButton;
    @BindView(R.id.CheckinButton)           Button       CheckinButton;
    @BindView(R.id.CheckoutButton)          Button       CheckoutButton;

    @BindView(R.id.drawer_layout)           DrawerLayout drawer;
    @BindView(R.id.left_drawer)             ListView     drawerlist; // FIXME: 21-5-2016 recview

    @BindView(R.id.MainList)                RecyclerView list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        draweritems = new String[]{
                "Taken",
                "Aanwezigheid",
                "Instellingen"
        };

        context = getApplicationContext();

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
                databaseHelper = new UitstelgedragOpenHelper(this, null);
                tasks = databaseHelper.getAll();
            //}
        //};
        //databaseloader.execute(null);

        adapter = new TaskAdapter(tasks, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        // Set the adapter for the list view
        drawerlist.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, R.id.drawer_item, draweritems));
        // Set the list's click listener
        drawerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class target  = null;
                switch (draweritems[position]){
                    case "Taken":
                        Log.i("DRAWER", "Taken");
                        //target = OverviewActivity.class;
                        return;
                    case "Aanwezigheid":
                        target = AttendanceActivity.class;
                        Log.i("DRAWER", "Aanwezigheid");
                        break;
                    case "Instellingen":
                        target = SettingsActivity.class;
                        Log.i("DRAWER", "Instellingen");
                        break;
                    default:
                        Log.i("DRAWER", ""+position);
                        return;
                }
                startActivity(new Intent(getBaseContext(), target));
            }
        });
    }


    @OnClick(R.id.ShowAttendancesLogButton) void submit() {
        Intent intent = new Intent(getBaseContext(), AttendanceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.AddTaskButton) void addtask(View v){
        //EditText cat = (EditText) findViewById(R.id.AddTaskCategory); // TODO: 29-4-2016
        EditText desc = (EditText) findViewById(R.id.AddTaskDescription);
        Task     task = new Task(desc.getText().toString());
        databaseHelper.addTask(task);
        adapter.addItem(adapter.getItemCount(), task);
        //adapter.notifyDataSetChanged();
        Log.i("Uitstelgedrag", "Persisted task #"+task.id);
        desc.setText("");
        desc.clearFocus();
    }

    @OnClick(R.id.CheckinButton) void checkIn(){
        dialog.show();
        getLocation();
    }

    @OnClick(R.id.CheckoutButton) void checkOut(View v){
        Timestamp         checkout = new Timestamp(Timestamp.DEPARTURE);
        String timestampstr = "Uitgecheckt om " + checkout.hours + ":" + checkout.minutes + ".";
        Snackbar.make(v, timestampstr, Snackbar.LENGTH_SHORT).show();
        databaseHelper.addTimestamp(Timestamp.DEPARTURE);
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
        databaseHelper.addTimestamp(Timestamp.ARRIVAL);
    }
}
