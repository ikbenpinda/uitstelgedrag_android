package achan.nl.uitstelgedrag.ui.presenters;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.AttendanceRepository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.gateways.AttendanceGateway;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by Etienne on 5-6-2016.
 */
public class AttendancePresenterImpl implements AttendancePresenter, OnLocationUpdatedListener, OnReverseGeocodingListener {

    Context context;
    AttendanceRepository database;

    Timestamp current;
    Location location;
    Address address;
    Callback success, error;

    public AttendancePresenterImpl(Context context) {
        this.context = context;
        this.database = new AttendanceGateway(new UitstelgedragOpenHelper(context, null).getWritableDatabase());
    }

    @Override
    public void checkIn(Timestamp timestamp, Callback onSuccess, Callback onError) {
        current = timestamp;

        // SmartLocation is async!
        success = onSuccess;
        error = onError;

        // Add a location to the timestamp.
        getPosition();
        success = () -> {
            database.insert(current);
            onSuccess.execute();
        };
    }

    @Override
    public Timestamp checkOut(Timestamp timestamp) {
        return database.insert(timestamp);
    }

    @Override
    public List<Timestamp> viewAttendance() {
        return database.getAll();
    }

    private void getPosition(){
        // Get GPS coordinates.
        SmartLocation.with(context).
                location()
                //.oneFix() // github issue > apparently broken?
                .start(this);
    }


    @Override
    public void onLocationUpdated(Location location) {
        this.location = location;

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
        String coordinates = this.location.getLongitude() + "/" + this.location.getLatitude();
        Log.w("LOCATIONSERVICE", "long/lat=" + coordinates);

        this.address = (list.isEmpty())? null: list.get(0);

        String position = (address != null)? address.getPostalCode() : coordinates;
        current.location = position;

        success.execute();
        //error.execute(); todo
    }

    public interface Callback {
        void execute();
    }
}
