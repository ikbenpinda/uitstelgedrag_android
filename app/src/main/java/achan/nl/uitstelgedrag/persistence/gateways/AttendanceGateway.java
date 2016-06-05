package achan.nl.uitstelgedrag.persistence.gateways;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.AttendanceRepository;
import achan.nl.uitstelgedrag.persistence.definitions.tables.AttendanceDefinition;

/**
 * Created by Etienne on 29-4-2016.
 */
public class AttendanceGateway implements AttendanceRepository {

    public static final int              COL_ID = 0;
    public static final int              COL_TYPE = 1;
    public static final int              COL_STR = 2;

    SQLiteDatabase database;

    public AttendanceGateway(SQLiteDatabase database) {
        this.database = database;
    }

    /**
     * Returns the timestamp from an inserted timestamp.toString().
     * @param timestampstr
     * @return Timestamp a Timestamp object devoid of type or id!
     */
    public Timestamp parseFrom(String timestampstr){

        // Make sure regular expressions are escaped when using String.split()!
        String[] fields = timestampstr.split("\\"+Timestamp.FIELD_DELIMITER);
        String date = fields[0];
        String time = fields[1];
        Log.i("Uitstelgedrag", "date="+date);
        Log.i("Uitstelgedrag", "time="+time);
        //String date = timestring.substring(0, timestring.indexOf(Timestamp.FIELD_DELIMITER)); // FIXME: 25-4-2016 empty str
        //String time = timestring.substring(timestring.indexOf(Timestamp.FIELD_DELIMITER) + 1, timestring.length());
        int day =       Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[0]);
        int month =     Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[1]);
        int year =      Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[2]);
        Log.i("Uitstelgedrag", "day=" + day);
        Log.i("Uitstelgedrag", "month=" + month);
        Log.i("Uitstelgedrag", "year=" + year);
        int hours =     Integer.parseInt(time.split("\\"+Timestamp.TIME_DELIMITER)[0]);
        int minutes =   Integer.parseInt(time.split("\\"+Timestamp.TIME_DELIMITER)[1]);
        Log.i("Uitstelgedrag", "hours/minutes="+hours+"/"+minutes);

        return new Timestamp(day, month, year, hours, minutes);
    }

    /**
     * Returns the query string for inserting a timestamp.
     * @param timestamp
     * @return
     */
    public String parseTo(Timestamp timestamp){
        StringBuilder querybuilder = new StringBuilder("INSERT INTO "+AttendanceDefinition.ATTENDANCES.name+"("+AttendanceDefinition.TYPE.name+", "+AttendanceDefinition.TIMESTAMP.name +") VALUES(")
                .append("\"")
                .append(timestamp.type)
                .append("\"")
                .append(",")
                .append("\"")
                .append(timestamp.toString())
                .append("\"")
                .append(")");
        return querybuilder.toString();
    }

    @Override
    public Timestamp get(int id) {
        Timestamp   timestamp;

        String query  = "SELECT * FROM " + AttendanceDefinition.ATTENDANCES.name + " WHERE " + AttendanceDefinition.ID.name + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        timestamp = parseFrom(cursor.getString(COL_STR)); //This returns a concatenated timestamp.
        timestamp.type = cursor.getString(COL_TYPE);
        timestamp.id = cursor.getInt(COL_ID);
        Log.i("Uitstelgedrag", "Selected attendance:" + timestamp.toString());
        cursor.close();

        return timestamp;
    }

    @Override
    public List<Timestamp> getAll() {
        List<Timestamp> timestamps = new ArrayList<>();
        Cursor          cur        = database.rawQuery("SELECT * FROM " + AttendanceDefinition.ATTENDANCES.name + "", null);

        cur.moveToPosition(-1);
        while (cur.moveToNext()){
            String timestring = cur.getString(COL_STR);
            Timestamp timestamp = parseFrom(timestring);
            timestamp.type = cur.getString(COL_TYPE);
            timestamp.id = cur.getInt(COL_ID);
            timestamps.add(timestamp);
            Log.i("Uitstelgedrag", "Added timestamp:" + timestamp.toString() + ", with id " + timestamp.id);
        }

        cur.close();
        return timestamps;
    }

    @Override
    public Timestamp insert(Timestamp object) {
        String query = parseTo(object);
        database.execSQL(query);
        Log.i("UITSTELGEDRAG", "Executing query " + query);
        Log.i("UITSTELGEDRAG", "Added timestamp " + object.toString());

        String getKey = "SELECT last_insert_rowid()";
        // note: You can't shorthand this to rawQuery.getInt because of the position.
        Cursor cursor = database.rawQuery(getKey, null);
        cursor.moveToFirst();
        object.id = cursor.getInt(COL_ID);
        cursor.close();
        Log.i("UITSTELGEDRAG", "Timestamp.id=" + object.id);

        return object;
    }

    @Override
    public boolean delete(Timestamp timestamp) {
        return false;
    }

    @Override
    public Timestamp update(Timestamp row) {
        Log.w("UITSTELGEDRAG", "Called update() but should've been calling insert()!");
        return row;
    }
}
