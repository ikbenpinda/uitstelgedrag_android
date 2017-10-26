package achan.nl.uitstelgedrag.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Attachments;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Attendances;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Notes;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Tasks;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Tasks_Labels;
import achan.nl.uitstelgedrag.persistence.migrations.Migration;

/**
 * So i'm guessing this is the Service Layer with all the needed repositories.
 *
 * Created by Etienne on 3-4-2016.
 */
public class UitstelgedragOpenHelper extends SQLiteOpenHelper implements Database {

    public static final String  DATABASE_NAME = "Uitstelgedrag.sqlite";
    public static final int     DATABASE_SCHEMA_VERSION = 32;

    public static final String CREATE = "CREATE TABLE IF NOT EXISTS ";

    /**
     * Adds a column to the given table.
     * @param table
     * @param column
     * @return
     */
    public static String ALTER_TABLE_ADD_COLUMN(Table table, Column column){
        return "ALTER TABLE " + table.name + " ADD COLUMN " + column.describe() + ";";
    }

    private Context context;

    // For executing custom SQL if needed.
    private final SQLiteDatabase sqlite;

    private final Table[] tables = new Table[]{
            Tasks.TABLE,
            Labels.TABLE,
            Tasks_Labels.TABLE,
            Attendances.TABLE,
            Notes.TABLE,
            Attachments.TABLE,
            Locations.TABLE
    };

    // FIXME: 18-4-2016 How to handle multiple attendances per day / attendances spanning multiple days?

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION);
        this.context = context;
        sqlite = getWritableDatabase();
    }

    public UitstelgedragOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_SCHEMA_VERSION, errorHandler);
        this.context = context;
        sqlite = getWritableDatabase();
    }

    /**
     * Drops all tables.
     * Unlike wipe this will destroy all data and the tables.
     * Think twice: this is nuke-from-orbit style clearing.
     * @param db
     */
    private void nuke(SQLiteDatabase db){
        Log.w("OpenHelper", "Upgraded/Downgraded sqlite, dropped tables.");

        for (Table table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
            Log.w("OpenHelper", "Dropped table " + table);
        }

        onCreate(db);
    }

    /**
     * Deletes all rows but not the tables.
     * Should typically only be called when the user wants to delete all data.
     */
    public void wipe(){
        Log.w("OpenHelper", "Wiping tables!");

        SQLiteDatabase database = getWritableDatabase();
        for (Table table : tables) {
            database.delete(table.name, null, null);
            Log.w("OpenHelper", "Deleted all rows from table " + table);
        }
    }

    /**
     * Shorthand for SELECT * FROM table WHERE column = key.
     * @param table
     * @param column
     * @param keys one or multiple keys to select by
     * @return
     */
    public Cursor query(Table table, Column column, String...keys){ // FIXME: 29-10-2016 Move SQLiteDatabase to wrapper class for query extension.
        return getWritableDatabase().query(table.name, null, column.name + " = ?", keys, null, null, null);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        for (Table table : tables){
            db.execSQL(CREATE + table.describe());
            Log.w("OpenHelper", "Creating table: " + table);
        }

        Log.w("OpenHelper", "Recreated tables!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //nuke(db); // Last resort solution to database migrations.

        Migration migration30to31 = database -> {
            Log.w("OpenHelper", "Migrating database version " + oldVersion + " to " + newVersion + ".");
//            StorageRepository ioStorage = new StorageGateway();
//            String path = ioStorage.exportData();
//            ioStorage.importData(path);
//            StringBuilder statementBuilder = new StringBuilder("ALTER TABLE");
//            statementBuilder
//                    .append(" ")
//                    .append(Labels.TABLE.name)
//                    .append(" ")
//                    .append("ADD COLUMN")
//                    .append(" ")
//                    .append(Labels.COLOR.describe())
//                    .append(";");
//            statementBuilder.toString()
            db.compileStatement(ALTER_TABLE_ADD_COLUMN(Labels.TABLE, Labels.COLOR)).execute();
        };

        Migration migration31to32 = database -> {
            Log.w("OpenHelper", "Migrating database version " + oldVersion + " to " + newVersion + ".");

            db.compileStatement(ALTER_TABLE_ADD_COLUMN(Locations.TABLE, Locations.CITY)).execute();
            db.compileStatement(ALTER_TABLE_ADD_COLUMN(Locations.TABLE, Locations.ADDRESS)).execute();
            db.compileStatement(ALTER_TABLE_ADD_COLUMN(Locations.TABLE, Locations.POSTAL_CODE)).execute();
        };

        migrate(migration31to32, db);
    }

    /**
     * Runs the changes needed to update the database.
     * Needed changes are defined in the given migration instance.
     * @param migration
     */
    public void migrate(Migration migration, SQLiteDatabase db){
        migration.migrate(db);
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
