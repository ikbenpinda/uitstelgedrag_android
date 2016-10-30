package achan.nl.uitstelgedrag.persistence.definitions.tables;

import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Table;

/**
 * Created by Etienne on 14-8-2016.
 */
public class Dayplans {
    public static final Column ID    = new Column("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final Column DATE  = new Column("date", "TEXT", "NOT NULL");
    public static final Table  TABLE = new Table("Dayplans", ID, DATE);
}