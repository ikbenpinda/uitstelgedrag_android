package achan.nl.uitstelgedrag.persistence.datasources;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.definitions.ColumnDefinition;
import achan.nl.uitstelgedrag.persistence.definitions.TableDefinition;

/**
 * Created by Etienne on 29-4-2016.
 */
public class AttendanceDataSource implements DataSource<Timestamp> {

    public static final int              COL_ID = 0;
    public static final int              COL_TYPE = 1;
    public static final int              COL_STR = 2;

    public static final ColumnDefinition ATTENDANCE_ID        = new ColumnDefinition("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final ColumnDefinition ATTENDANCE_TYPE      = new ColumnDefinition("type", "TEXT", "NULL");
    public static final ColumnDefinition ATTENDANCE_TIMESTAMP = new ColumnDefinition("timestamp", "TEXT", "NULL");
    public static final TableDefinition  ATTENDANCES          = new TableDefinition("Attendances", ATTENDANCE_ID, ATTENDANCE_TYPE, ATTENDANCE_TIMESTAMP) ;

    SQLiteDatabase database;

    public AttendanceDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    /**
     * Returns the timestamp from an inserted timestamp.toString().
     * @param timestampstr
     * @return Timestamp a Timestamp object devoid of type or id!
     */
    public Timestamp parseFrom(String timestampstr){
        Timestamp timestamp = new Timestamp();

        // Make sure regular expressions are escaped when using String.split()!
        String[] fields = timestampstr.split("\\"+Timestamp.FIELD_DELIMITER);
        String date = fields[0];
        String time = fields[1];
        Log.i("Uitstelgedrag", "date="+date);
        Log.i("Uitstelgedrag", "time="+time);
        //String date = timestring.substring(0, timestring.indexOf(Timestamp.FIELD_DELIMITER)); // FIXME: 25-4-2016 empty str
        //String time = timestring.substring(timestring.indexOf(Timestamp.FIELD_DELIMITER) + 1, timestring.length());
        timestamp.day =       Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[0]);
        timestamp.month =     Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[1]);
        timestamp.year =      Integer.parseInt(date.split("\\"+Timestamp.DATE_DELIMITER)[2]);
        Log.i("Uitstelgedrag", "day=" + timestamp.day);
        Log.i("Uitstelgedrag", "month=" + timestamp.month);
        Log.i("Uitstelgedrag", "year=" + timestamp.year);
        timestamp.hours =     Integer.parseInt(time.split("\\"+Timestamp.TIME_DELIMITER)[0]);
        timestamp.minutes =   Integer.parseInt(time.split("\\"+Timestamp.TIME_DELIMITER)[1]);
        Log.i("Uitstelgedrag", "hours/minutes="+timestamp.hours+"/"+timestamp.minutes);

        return timestamp;
    }

    /**
     * Returns the query string for inserting a timestamp.
     * @param timestamp
     * @return
     */
    public String parseTo(Timestamp timestamp){
        StringBuilder querybuilder = new StringBuilder("INSERT INTO Attendances(type, timestamp) VALUES(")
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

        String query  = "SELECT * FROM Attendances WHERE id = " + id;
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
        Cursor          cur        = database.rawQuery("SELECT * FROM Attendances", null);

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
    public boolean delete(int id) {
        return false;
    }

    @Override
    public void update(Timestamp row) {
        Log.w("UITSTELGEDRAG", "Called update() but should've been calling insert()!");
    }
}
