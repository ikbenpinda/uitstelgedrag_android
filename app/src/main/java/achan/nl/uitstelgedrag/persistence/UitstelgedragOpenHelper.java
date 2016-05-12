package achan.nl.uitstelgedrag.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.models.Task;
import achan.nl.uitstelgedrag.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.datasources.AttendanceDataSource;
import achan.nl.uitstelgedrag.persistence.datasources.CategoryDataSource;
import achan.nl.uitstelgedrag.persistence.datasources.TaskDataSource;

/**
 * Created by Etienne on 3-4-2016.
 */
public class UitstelgedragOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Uitstelgedrag.database";
    public static final int DATABASE_SCHEMA_VERSION = 6;

    private final SQLiteDatabase database;

    private TaskDataSource taskDataSource;
    private CategoryDataSource categoryDataSource;
    private AttendanceDataSource attendanceDataSource;

    // FIXME: 18-4-2016 How to handle multiple attendances per day / attendances spanning multiple days?

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION);
    }

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION, errorHandler);
    }

    {   // Initializer block.
        database = getWritableDatabase();
        taskDataSource = new TaskDataSource(database);
        categoryDataSource = new CategoryDataSource(database);
        attendanceDataSource = new AttendanceDataSource(database);
    }

    /**
     *
     * @return the row id of the last insert.
     */
    public int getLastRow_id(){
        String getKey = "SELECT last_insert_rowid()";
        Cursor cursor = database.rawQuery(getKey, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public List<Task> getAll(){
        return taskDataSource.getAll();
    }

    public void addTask(Task task) {
        taskDataSource.insert(task);
    }

    public void deleteTask(Task task){
        Log.w("XXXXXXXXX", ""+task.id);
        taskDataSource.delete(task.id);
    }

    private void nuke(SQLiteDatabase db){
        Log.w("OpenHelper", "Upgraded/Downgraded database, deleted tables.");
        db.delete("Tasks", null,null);
        db.delete("Categories", null,null);
        db.delete("Attendances", null,null);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + taskDataSource.TABLE_TASKS;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + categoryDataSource.CATEGORIES;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + attendanceDataSource.ATTENDANCES;
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        nuke(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        nuke(db);
    }

    /**
     *
     * @param type Timestamp.ARRIVAL or Timestamp.DEPARTURE.
     */
    public void addTimestamp(String type) {
        attendanceDataSource.insert(new Timestamp(type));
    }

    /**
     * Gets all attendances from the database.
     *
     * IDs are a bit tricky to work with tbh.
     */
    public List<Timestamp> getTimestamps(){
        return attendanceDataSource.getAll();
    }
}
