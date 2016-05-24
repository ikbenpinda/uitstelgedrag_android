package achan.nl.uitstelgedrag.persistence;

import android.content.Context;
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

    public static final String DATABASE_NAME = "Uitstelgedrag.sqlite";
    public static final int DATABASE_SCHEMA_VERSION = 6;

    private final SQLiteDatabase sqlite;

    public TaskDataSource       tasks;
    public CategoryDataSource   categories;
    public AttendanceDataSource attendances;

    // FIXME: 18-4-2016 How to handle multiple attendances per day / attendances spanning multiple days?

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION);
    }

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION, errorHandler);
    }

    {   // Initializer block.
        sqlite = getWritableDatabase();
        tasks = new TaskDataSource(sqlite);
        categories = new CategoryDataSource(sqlite);
        attendances = new AttendanceDataSource(sqlite);
    }

    public List<Task> getAll(){
        return tasks.getAll();
    }

    public void addTask(Task task) {
        tasks.insert(task);
    }

    public void deleteTask(Task task){
        Log.w("XXXXXXXXX", ""+task.id);
        tasks.delete(task.id);
    }

    private void nuke(SQLiteDatabase db){
        Log.w("OpenHelper", "Upgraded/Downgraded sqlite, deleted tables.");
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
        String query = "CREATE TABLE IF NOT EXISTS " + tasks.TABLE_TASKS;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + categories.CATEGORIES;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + attendances.ATTENDANCES;
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
        attendances.insert(new Timestamp(type));
    }

    /**
     * Gets all attendances from the sqlite.
     *
     * IDs are a bit tricky to work with tbh.
     */
    public List<Timestamp> getTimestamps(){
        return attendances.getAll();
    }
}
