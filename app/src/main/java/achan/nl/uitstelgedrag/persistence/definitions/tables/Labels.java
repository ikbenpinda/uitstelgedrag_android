package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Created by Etienne on 5-6-2016.
 */
public class Labels {

    public static final Column ID    = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column TITLE = new Column("title", Types.TEXT, Constraints.NOT_NULL);
    public static final Column TASK  = new Column("task_id", Types.TEXT, Constraints.FOREIGN_KEY_REFERENCES(Tasks.TABLE, Tasks.ID));
    public static final Table  TABLE = new Table("Labels", ID, TITLE, TASK);

    /**
     * Fills a ContentValues object with the given model.
     * @param label
     * @return
     */
    public static ContentValues toValues(Task task, Label label){
        ContentValues values = new ContentValues();

        if (label.id > 0)
            values.put(ID.name, label.id);
        values.put(TITLE.name, label.title);

        if (task != null)
            values.put(TASK.name, task.id);

        return values;
    }

    /**
     * Fills the object from the cursor.
     *
     * @param cursor
     * @return
     */
    public static List<Label> fromCursor(Cursor cursor){

        List<Label> labels = new ArrayList<>();

        while (cursor.moveToNext()) {

            Label label = new Label();
            label.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID.name));
            label.title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE.name));
            labels.add(label);
        }

        return labels;
    }
}
