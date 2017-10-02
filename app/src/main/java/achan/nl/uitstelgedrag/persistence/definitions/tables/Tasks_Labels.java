package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.database.Cursor;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Created by Etienne on 02/02/17.
 */

public class Tasks_Labels {

    public static final Column ID = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY);
    public static final Column TASK_ID = new Column("task_id", Types.INTEGER, Constraints.FOREIGN_KEY_REFERENCES(Tasks.TABLE, Tasks.ID, true));
    public static final Column LABEL_ID = new Column("label_id", Types.INTEGER, Constraints.FOREIGN_KEY_REFERENCES(Labels.TABLE, Labels.ID, true));
    public static final Table TABLE = new Table("Tasks_Labels", ID, TASK_ID, LABEL_ID);

    /**
     *
     */
    public static void fromCursor(Cursor cursor){
        // todo
    }

    /**
     *
     */
    public static void toValues(Task task, Label label){
        // todo
    }

}
