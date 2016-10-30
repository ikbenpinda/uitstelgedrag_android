package achan.nl.uitstelgedrag.persistence.definitions;

import android.util.Log;

/**
 * Created by Etienne on 3-4-2016.
 */
public class Column {
    public String name;
    public String type; // todo - @string-def
    public String constraints; // todo - @string-def

    public Column(String name, String type, String constraints) {
        this.name = name;
        this.type = type;
        this.constraints = constraints;
    }

    /**
     * Default constructor for columns without constraints.
     * Constraints will default to "NULL".
     * @param name
     * @param type
     */
    public Column(String name, String type) {
        this.name = name;
        this.type = type;
        this.constraints = "NULL";
    }

    public String describe(){
        String description = String.format("%s %s %s ", name, type, constraints);
        Log.i("Column", "Describing " + name + ":" + description);
        return description;
    }

    @Override
    public String toString() {
        // todo differentiate from describe.
        //return String.format("%s %s %s", name, type, constraints);
        return name;
    }
}
