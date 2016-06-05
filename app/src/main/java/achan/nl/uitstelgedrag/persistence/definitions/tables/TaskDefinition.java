package achan.nl.uitstelgedrag.persistence.definitions.tables;

import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Table;

/**
 * Created by Etienne on 4-6-2016.
 */
public class TaskDefinition {

    public static final Column ID          = new Column("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final Column DESCRIPTION = new Column("description", "TEXT", "NOT NULL");
    public static final Column CATEGORY_ID = new Column("CATEGORY_ID", "INTEGER", "NULL");
    public static final Table  TASKS       = new Table("Tasks", ID, DESCRIPTION, CATEGORY_ID);

}
