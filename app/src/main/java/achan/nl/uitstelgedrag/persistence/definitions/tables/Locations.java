package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Location;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Definition for a saved location.
 * address lines differ per country, so the fields CITY / ADDRESS / POSTAL CODE
 * are used to represent that.
 *
 * Created by Etienne on 15/01/17.
 */
public class Locations {

    public static final Column ID = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column LONGITUDE = new Column("longitude", Types.REAL, Constraints.NOT_NULL);
    public static final Column LATITUDE = new Column("latitude", Types.REAL, Constraints.NOT_NULL);

    /**
     * Also known as the Locality / Sub-admin by the geocoder.
     */
    public static final Column CITY = new Column("city", Types.TEXT);

    /**
     * Also known as Thoroughfare by the geocoder.
     */
    public static final Column ADDRESS = new Column("address", Types.TEXT);

    public static final Column POSTAL_CODE = new Column("postal_code", Types.TEXT);
    public static final Column LABEL_ID = new Column("label_id", Types.REAL, Constraints.FOREIGN_KEY_REFERENCES(Labels.TABLE, Labels.ID, false));

    public static final Table TABLE = new Table("locations", ID, LONGITUDE, LATITUDE, CITY, ADDRESS, POSTAL_CODE, LABEL_ID);

    public static ContentValues toValues(Label label, Location location){
        ContentValues values = new ContentValues();

        values.put(LONGITUDE.name, location.longitude);
        values.put(LATITUDE.name, location.latitude);

        if (location.city != null)
            values.put(CITY.name, location.city);

        if (location.address != null)
            values.put(ADDRESS.name, location.address);

        if (location.postalCode != null)
            values.put(POSTAL_CODE.name, location.postalCode);

        if (label != null)
            values.put(LABEL_ID.name, label.id);

        return values;
    }

    public static Location fromCursor(Cursor cursor){
        Location location = new Location();

        if (!cursor.moveToNext())
            return location;

        location.longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE.name));
        location.latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE.name));

        location.city = cursor.getString(cursor.getColumnIndexOrThrow(CITY.name));
        location.address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS.name));
        location.postalCode = cursor.getString(cursor.getColumnIndexOrThrow(POSTAL_CODE.name));

        return location;
    }
}
