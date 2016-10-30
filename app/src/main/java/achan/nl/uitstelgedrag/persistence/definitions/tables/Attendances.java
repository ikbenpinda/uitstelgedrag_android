package achan.nl.uitstelgedrag.persistence.definitions.tables;

import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Table;

/**
 * Created by Etienne on 5-6-2016.
 */
public class Attendances {

    public static final Column ID        = new Column("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final Column TYPE      = new Column("type", "TEXT", "NULL");
    public static final Column TIMESTAMP = new Column("timestamp", "TEXT", "NULL");
    public static final Table  TABLE     = new Table("Attendances", ID, TYPE, TIMESTAMP) ;
}
