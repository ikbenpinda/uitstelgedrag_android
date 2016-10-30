package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Represents a database table.
 *
 * Created by Etienne on 3-4-2016.
 */
public class Table {

    public String   name;
    public Column[] columns;

    public Table(String name, Column... columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * returns a table description.
     * @return
     */
    public String describe(){

        StringBuilder sb = new StringBuilder(name).append("(");

        for (Column column : columns) {
            sb.append(column.describe());
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");

        return sb.toString();
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }
}
