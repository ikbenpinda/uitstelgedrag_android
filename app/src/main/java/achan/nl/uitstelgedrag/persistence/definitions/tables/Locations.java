package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Created by Etienne on 15/01/17.
 */
public class Locations {

    public static final Column ID = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column LONGITUDE = new Column("longitude", Types.REAL, Constraints.NOT_NULL);
    public static final Column LATITUDE = new Column("latitude", Types.REAL, Constraints.NOT_NULL);
    public static final Column LABEL_ID = new Column("label_id", Types.REAL, Constraints.FOREIGN_KEY_REFERENCES(Labels.TABLE, Labels.ID, false));

    public static final Table TABLE = new Table("locations", ID, LONGITUDE, LATITUDE, LABEL_ID);

    public static ContentValues toValues(Label label, Location location){
        ContentValues values = new ContentValues();

        values.put(LONGITUDE.name, location.getLongitude());
        values.put(LATITUDE.name, location.getLatitude());

        if (label != null && label.id != 0)
            values.put(LABEL_ID.name, label.id);

        return values;
    }

    public static Location fromCursor(Cursor cursor){
        Location location = new Location("");

        if (!cursor.moveToNext())
            return location;

        location.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE.name)));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE.name)));

        return location;
    }
}
