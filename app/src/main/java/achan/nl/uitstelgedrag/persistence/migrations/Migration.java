package achan.nl.uitstelgedrag.persistence.migrations;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Etienne on 3-10-2016.
 */
public interface Migration {

    void migrate(SQLiteDatabase database);
}
