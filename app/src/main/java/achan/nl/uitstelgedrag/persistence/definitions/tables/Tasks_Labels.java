package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Keeps track of labelled items.
 *
 * Reason for using a third table:
 * a. Allows for easy lookups from both sides;
 *     1. X items with label Y.
 *     2. Y Labels for item X.
 * b.Easily expandable for different categories.
 *
 * Created by Etienne on 02/02/17.
 */
public class Tasks_Labels {

    public static final int DEFAULT_ID_VALUE = 0;

    public static final int DATA_ID = 0;
    public static final int DATA_TASK_ID = 1;
    public static final int DATA_LABEL_ID = 2;

    public static final Column ID = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY);
    public static final Column TASK_ID = new Column("task_id", Types.INTEGER, Constraints.FOREIGN_KEY_REFERENCES(Tasks.TABLE, Tasks.ID, true));
    public static final Column LABEL_ID = new Column("label_id", Types.INTEGER, Constraints.FOREIGN_KEY_REFERENCES(Labels.TABLE, Labels.ID, true));
    public static final Table TABLE = new Table("Tasks_Labels", ID, TASK_ID, LABEL_ID);

    /**
     * Returns ContentValues for a task-label pair.
     */
    public static ContentValues toValues(Task task, Label label){
        ContentValues values = new ContentValues();

        values.put(TASK_ID.name, task.id);
        values.put(LABEL_ID.name, label.id);

        return values;
    }

    /**
     * Returns a list of pairings.
     * One tuple contains three columns: id, task_id, and label_id, in that order.
     */
    public static List<int[]> fromCursor(Cursor cursor){

        List<int[]> data = new ArrayList<>();

        while(cursor.moveToNext()) {
            int[] tuple = new int[3];
            tuple[DATA_ID] = cursor.getInt(cursor.getColumnIndexOrThrow(ID.name));
            tuple[DATA_TASK_ID] = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID.name));
            tuple[DATA_LABEL_ID] = cursor.getInt(cursor.getColumnIndexOrThrow(LABEL_ID.name));
            data.add(tuple);
        }

        return data;
    }

}
