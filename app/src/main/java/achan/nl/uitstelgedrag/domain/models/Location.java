package achan.nl.uitstelgedrag.domain.models;

import android.location.Address;

/**
 * Represents a standard location,
 * with additional fields for reverse geocoding.
 *
 * Created by Etienne on 15/12/16.
 */
public class Location { // todo location API.

    /**
     * This can be the default AddressLine as returned
     * by the geocoder, or any user defined name for this location,
     * e.g: "Home".
     */
    public String name = "";
    public double latitude;
    public double longitude;
    public String city = null;
    public String address = null;
    public String postalCode = null;

    public Location() {
    }

    public Location(Address address){
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.city = address.getLocality();
        this.address = address.getThoroughfare();
        this.postalCode = address.getPostalCode();
        this.name = address.getThoroughfare() + ", " + address.getPostalCode() + ", " + address.getLocality();
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, String city, String address, String postalCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
