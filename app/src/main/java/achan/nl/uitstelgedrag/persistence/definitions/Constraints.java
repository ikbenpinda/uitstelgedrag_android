package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Table constraint definitions for SQLite.
 * https://www.sqlite.org/syntax/table-constraint.html
 *
 * Created by Etienne on 3-4-2016.
 */
public class Constraints {

    public static final String PRIMARY_KEY_AUTOINCREMENT = "PRIMARY KEY AUTOINCREMENT ";
    public static final String PRIMARY_KEY = "PRIMARY KEY ";

    public static final String UNIQUE = "UNIQUE ";
    public static final String NULL = "NULL ";
    public static final String NOT_NULL = "NOT NULL ";

    public static final String CASCADE = "CASCADE ";
    public static final String SET_NULL = "SET NULL ";
    public static final String SET_DEFAULT = "SET DEFAULT ";

    public enum DeleteOrUpdateActions {
        CASCADE("CASCADE "),
        SET_NULL("SET NULL "),
        SET_DEFAULT("SET DEFAULT ");

        String action;

        DeleteOrUpdateActions(String action) {
            this.action = action;
        }
    }


    /**
     * Sets the foreign key to the referenced column, with ON DELETE CASCADE by default.
     * @param other
     * @param key
     * @return
     */
    public static String FOREIGN_KEY_REFERENCES(Table other, Column key){
        return String.format("REFERENCES %s(%s) ON DELETE CASCADE ", other, key);
    }

    /**
     * Sets the foreign key to the referenced column, optionally with or without ON DELETE CASCADE
     * @param other
     * @param key
     * @return
     */
    public static String FOREIGN_KEY_REFERENCES(Table other, Column key, boolean cascadeOnDelete){
        if (cascadeOnDelete)
            return String.format("REFERENCES %s(%s) ON DELETE CASCADE ", other, key);
        else
            return String.format("REFERENCES %s(%s) ", other, key);
    }

    public static String ON_DELETE(DeleteOrUpdateActions action){
        return String.format("ON DELETE %s ", action.action);
    }

    public static String ON_UPDATE(DeleteOrUpdateActions action){
        return String.format("ON UPDATE %s ", action.action);
    }
}
