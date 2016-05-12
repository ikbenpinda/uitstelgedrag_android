package achan.nl.uitstelgedrag.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.util.Log;

import java.util.List;
import java.util.Observable;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.utils.LocationState;

/**
 * Created by Etienne on 9-5-2016.
 */
public class LocationRequestHandler extends Observable
        implements OnLocationUpdatedListener, OnReverseGeocodingListener{

    Context context;
    Location location;
    Address address; // todo Read documentation; Provider returns a list everywhere.

    public LocationRequestHandler(Context context) {
        this.context = context;

        LocationState state = SmartLocation.with(context).location().state();
        StringBuilder locationstatus = new StringBuilder()
                .append("{")

                // Check if the location services are enabled
                .append("LocationServices enabled=")
                .append(state.locationServicesEnabled())

                // Check if any provider (network or gps) is enabled
                .append("Provider available=")
                .append(state.isAnyProviderAvailable())

                // Check if GPS is available
                .append("GPS available=")
                .append(state.isGpsAvailable())

                // Check if Network is available
                .append("Network available=")
                .append(state.isNetworkAvailable())

                // Check if the passive provider is available
                .append("Passive available=")
                .append(state.isPassiveAvailable())

                // Check if the location is mocked
                .append("MockSetting enabled=")
                .append(state.isMockSettingEnabled())
                .append("}");
                Log.i("LOCATIONSERVICE", locationstatus.toString());
    }

    /**
     * Returns the GPS coordinates.
     * @return
     */
    public void getLocation() {

        // Get GPS coordinates.
        SmartLocation.with(context).
                location()
                //.oneFix() // github issue > apparently broken?
                .start(this);
    }

    /**
     * Translates the GPS coordinates to an address.
     * @return
     */
    public void getAddress(){

        // Translate GPS coordinates to street name.
        SmartLocation.with(context)
                .geocoding()
                .reverse(location, this);
    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.i("LOCATIONSERVICE", "Received location!");
        this.location = location;
        setChanged();
        notifyObservers();
    }

    @Override
    public void onAddressResolved(Location location, List<Address> list) {
        Log.i("LOCATIONSERVICE", "Received address!");
        address = list.get(0);
        setChanged();
        notifyObservers();
    }
}
