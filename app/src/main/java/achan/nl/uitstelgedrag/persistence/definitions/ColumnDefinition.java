package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Created by Etienne on 3-4-2016.
 */
public class ColumnDefinition {
    public String name;
    public String type;
    public String constraints;

    public ColumnDefinition(String name, String type, String constraints) {
        this.name = name;
        this.type = type;
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return new StringBuilder("")
                .append(name)
                .append(" ")
                .append(type)
                .append(" ")
                .append(constraints)
                .append(" ")
                .toString();
    }
}
