package achan.nl.uitstelgedrag.persistence.datasources;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Etienne on 29-4-2016.
 */
public class DataSourceFactory {

    public static final String TASKS = "tasks";
    public static final String CATEGORIES = "categories";
    public static final String ATTENDANCES = "attendances";

    SQLiteDatabase database;

    public DataSourceFactory(SQLiteDatabase database) {
        this.database = database;
    }

    public DataSource make(String type){
        DataSource source = null;

        switch (type){
            case TASKS:
                source = new TaskDataSource(database);
                break;
            case CATEGORIES:
                source = new CategoryDataSource(database);
                break;
            case ATTENDANCES:
                source = new AttendanceDataSource(database);
                break;
            default:
                Log.w("DataSourceFactory","No valid case for make().");
                break;
        }

        return source;
    }
}
