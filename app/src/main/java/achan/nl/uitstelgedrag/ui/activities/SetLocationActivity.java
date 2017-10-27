package achan.nl.uitstelgedrag.ui.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Location;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;
import achan.nl.uitstelgedrag.persistence.gateways.LabelGateway;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;

public class SetLocationActivity extends Base implements OnMapReadyCallback{

    @BindView(R.id.edit_label_title) EditText labelTitle;
    @BindView(R.id.location_address_edittext) EditText addressField;
    @BindView(R.id.location_use_current_checkbox) CheckBox useCurrentLocationCheckbox;
    @BindView(R.id.location_cancel_button) Button cancelButton;
    @BindView(R.id.location_save_button) Button saveButton;
    @BindView(R.id.location_delete_button) Button deleteButton;

    // Zoom levels. For the spec, see here: https://developers.google.com/maps/documentation/javascript/tutorial#zoom-levels.
    public static final int ZOOM_LEVEL_CITY = 10;
    public static final int ZOOM_LEVEL_STREETS = 15;
    public static final int ZOOM_LEVEL_BUILDINGS = 20;

    private Context context;
    private GoogleMap map;
    private Location lastLocation;
    private Label lastLabel;
    private boolean isInLabelEditingMode = false;

    @Override
    Activities getCurrentActivity() {
        return Activities.SETLOCATION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        ContentValues labelValues = intent.getParcelableExtra("label");
        isInLabelEditingMode = labelValues != null;
        if (isInLabelEditingMode) {
            deleteButton.setVisibility(View.VISIBLE);
            int label_id = labelValues.getAsInteger(Labels.ID.name);
            Label label = new LabelGateway(this).get(label_id);
            initializeLabelEditingMode(label);
        }

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
        saveButton.setOnClickListener(v -> {
            if (isInLabelEditingMode)
                exitActivity(lastLabel);
            else
                exitActivity(lastLocation);
        });
        cancelButton.setOnClickListener(v -> exitActivity(null));
        deleteButton.setOnClickListener(v -> {
            new LabelGateway(this).delete(lastLabel);
            exitActivity(null);
        });
    }

    private void exitActivity(Object data) {
        Intent intent;

        if (data == null)
            intent = returnWithAddress(null);
        else
            intent = isInLabelEditingMode?
                    returnWithUpdatedLabel(lastLabel):
                    returnWithAddress(lastLocation);

        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        useCurrentLocation();
    }

    private void initializeLabelEditingMode(Label label){

        lastLabel = label;

        labelTitle.setText(lastLabel.title);
        labelTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lastLabel.title = s.toString();
            }
        });

        if (lastLabel.color != null)
            labelTitle.setTextColor(Integer.parseInt(lastLabel.color));
        // todo - color changed? choose from picker : leave as is.

        if (!lastLabel.location.name.isEmpty())
            addressField.setText(lastLabel.location.name);
        // todo - update lastLabel.location with lastLocation automatically / listener pattern.

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

    private Intent returnWithAddress(Location location){
        Intent returnAddressIntent = new Intent(this, Activities.TASKS.activity);
        if (location != null) {
            Log.i("SetLocationActivity", "Returning location for address: " + location.toString());
            returnAddressIntent.putExtra("location", Locations.toValues(null, location));
        }
        return returnAddressIntent;
    }

    private Intent returnWithUpdatedLabel(Label label){

        label.location = lastLocation;
        label.location.name = lastLocation.address + ", " + lastLocation.postalCode + ", " + lastLocation.city; // FIXME: 27-10-2017 move to location class function.
        new LabelGateway(this).update(label);
        Intent updateLabelIntent = new Intent(this, Activities.TASKS.activity);

        return updateLabelIntent;
    }
}
