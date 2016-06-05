package achan.nl.uitstelgedrag.persistence.definitions.tables;

import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Table;

/**
 * Created by Etienne on 5-6-2016.
 */
public class CategoryDefinition {

    public static final Column ID         = new Column("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final Column TITLE      = new Column("title", "TEXT", "NOT NULL");
    public static final Table  CATEGORIES = new Table("Categories", ID, TITLE);
}
