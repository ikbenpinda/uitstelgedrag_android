package achan.nl.uitstelgedrag.ui.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Location;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;

public class SetLocationActivity extends Base implements OnMapReadyCallback{

    @BindView(R.id.location_address_edittext) EditText addressField;
    @BindView(R.id.location_use_current_checkbox) CheckBox useCurrentLocationCheckbox;
    @BindView(R.id.location_cancel_button) Button cancelButton;
    @BindView(R.id.location_save_button) Button saveButton;

    // Zoom levels. For the spec, see here: https://developers.google.com/maps/documentation/javascript/tutorial#zoom-levels.
    public static final int ZOOM_LEVEL_CITY = 10;
    public static final int ZOOM_LEVEL_STREETS = 15;
    public static final int ZOOM_LEVEL_BUILDINGS = 20;

    private Context context;
    private GoogleMap map;
    private Location lastLocation;

    @Override
    Activities getCurrentActivity() {
        return Activities.SETLOCATION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        addressField.addTextChangedListener(new TextWatcher() {

            boolean changed = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3)
                    changed = true;
                else
                    changed = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (changed)
                    findGPSCoordinatesForAddress(s.toString());
            }
        });

        useCurrentLocationCheckbox.setOnClickListener(v -> useCurrentLocation());
        saveButton.setOnClickListener(v -> returnAddress(lastLocation));
        cancelButton.setOnClickListener(v -> returnAddress(null));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        useCurrentLocation();
    }

    private void findLocationOnMap(){
        SmartLocation.with(context).location().oneFix().start(location -> {
            LatLng youAreHere = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(youAreHere)
                    .title("U bevindt zich hier."));
            map.moveCamera(CameraUpdateFactory.newLatLng(youAreHere));
            map.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL_STREETS));
        });
    }

    private void findGPSCoordinatesForAddress(String address){
        //geocode address
        SmartLocation.with(context).geocoding().direct(address, (s, list) -> {

            if (list.isEmpty())
                return;

            LocationAddress addressMatch = list.get(0);
            Log.i("SetLocationActivity", "Address found: " + addressMatch.getAddress().getAddressLine(0));

            // Get GPS coordinates for user-defined marker.
            LatLng addressCoordinates = new LatLng(addressMatch.getLocation().getLatitude(), addressMatch.getLocation().getLongitude());

            // Display marker on position.
            map.addMarker(new MarkerOptions().position(addressCoordinates).title(addressMatch.getAddress().getAddressLine(0)));

            // Re-center camera.
            map.moveCamera(CameraUpdateFactory.newLatLng(addressCoordinates));

            // Adjust zoom level for accuracy.
            float zoomLevel = ZOOM_LEVEL_CITY;

            if (addressMatch.getAddress().getLocality() != null)
                zoomLevel = ZOOM_LEVEL_CITY;
            if (addressMatch.getAddress().getPostalCode() != null)
                zoomLevel = ZOOM_LEVEL_STREETS + 1;
            if (addressMatch.getAddress().getThoroughfare() != null)
                zoomLevel = ZOOM_LEVEL_BUILDINGS - 2;

            // Zoom accordingly.
            map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

            updateLastLocation(addressMatch.getAddress());
        });
    }

    private void useCurrentLocation(){
        findLocationOnMap();
        SmartLocation.with(context).location().oneFix().start(location -> {
            SmartLocation.with(context).geocoding().reverse(location, (location1, list) -> {
                String addressString = list.size() > 0 ? list.get(0).getAddressLine(0) : "Niet gevonden";
                addressField.setText(addressString);
                updateLastLocation(list.get(0));
            });
        });
    }

    private void updateLastLocation(Address address) {
        lastLocation = new Location(address);
    }

    private void returnAddress(Location location){
        Intent returnAddressIntent = new Intent(this, Activities.TASKS.activity);
        if (location != null) {
            Log.i("SetLocationActivity", "Returning location for address: " + location.toString());
            returnAddressIntent.putExtra("location", Locations.toValues(null, location));
        }
        startActivity(returnAddressIntent);
    }
}
