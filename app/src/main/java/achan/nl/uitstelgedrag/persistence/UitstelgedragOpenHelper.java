package achan.nl.uitstelgedrag.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import achan.nl.uitstelgedrag.persistence.definitions.tables.AttendanceDefinition;
import achan.nl.uitstelgedrag.persistence.definitions.tables.CategoryDefinition;
import achan.nl.uitstelgedrag.persistence.definitions.tables.TaskDefinition;

/**
 * So i'm guessing this is the Service Layer with all the needed repositories.
 *
 * Created by Etienne on 3-4-2016.
 */
public class UitstelgedragOpenHelper extends SQLiteOpenHelper implements Database {

    public static final String  DATABASE_NAME = "Uitstelgedrag.sqlite";
    public static final int     DATABASE_SCHEMA_VERSION = 7;

    private final SQLiteDatabase sqlite;

    // FIXME: 18-4-2016 How to handle multiple attendances per day / attendances spanning multiple days?

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION);
    }

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION, errorHandler);
    }

    {   // Initializer block.
        sqlite = getWritableDatabase();
    }

    private void nuke(SQLiteDatabase db){
        Log.w("OpenHelper", "Upgraded/Downgraded sqlite, deleted tables.");
        db.delete(TaskDefinition.TASKS.name, null,null);
        db.delete(CategoryDefinition.CATEGORIES.name, null,null);
        db.delete(AttendanceDefinition.ATTENDANCES.name, null,null);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TaskDefinition.TASKS;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + CategoryDefinition.CATEGORIES;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + AttendanceDefinition.ATTENDANCES;
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

    @Override
    public int getSchemaVersion() {
        return DATABASE_SCHEMA_VERSION;
    }
}
