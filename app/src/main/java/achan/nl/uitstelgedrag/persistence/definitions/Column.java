package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Created by Etienne on 3-4-2016.
 */
public class Column {
    public String name;
    public String type;
    public String constraints;

    public Column(String name, String type, String constraints) {
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
