package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Created by Etienne on 4-6-2016.
 */
public class Tasks{

    public static final int DEFAULT_ID_VALUE = 0;

    public static final Column ID          = new Column("id",          Types.INTEGER,   Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column DESCRIPTION = new Column("description", Types.TEXT,      Constraints.NOT_NULL);
    public static final Column CREATED_ON  = new Column("created_on",  Types.TEXT,      Constraints.NULL);
    public static final Column DEADLINE    = new Column("deadline",    Types.TEXT,      Constraints.NULL);
    public static final Table  TABLE       = new Table("Tasks", ID, DESCRIPTION, CREATED_ON, DEADLINE);

    /**
     * Fills a ContentValues object with the given model.
     * @param task
     * @return
     */
    public static ContentValues toValues(Task task){

        ContentValues values = new ContentValues();

        if (task.id > DEFAULT_ID_VALUE) // Use the set id if assigned, or autoincrement.
            values.put(ID.name, task.id);

        values.put(DESCRIPTION.name, task.description);

        if (!task.labels.isEmpty()) {
            Log.w("Tasks", "Labels will not be inserted, use a seperate query!");
        }
        if (task.createdOn != null)
            values.put(CREATED_ON.name, Timestamp.formatDate(task.createdOn));
        if (task.deadline != null)
            values.put(DEADLINE.name, Timestamp.formatDate(task.deadline));

        return values;
    }

    /**
     * Fills the object from the cursor.
     *
     * @param cursor
     * @return
     */
    public static Task fromCursor(Cursor cursor){
        Task task = new Task();

        task.id          = cursor.getInt(cursor.getColumnIndexOrThrow(ID.name));
        task.description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION.name));
        task.createdOn   = Timestamp.formatDate(cursor.getString(cursor.getColumnIndexOrThrow(CREATED_ON.name)));
        task.deadline    = Timestamp.formatDate(cursor.getString(cursor.getColumnIndexOrThrow(DEADLINE.name)));

        return task;
    }

}
