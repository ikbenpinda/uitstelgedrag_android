package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Created by Etienne on 3-4-2016.
 */
public class Table {
    public String   name;
    public Column[] columns;

    public Table(String name, Column... columns) {
        this.name = name;
        this.columns = columns;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append("(");
        for (Column column : columns) {
            sb.append(column.toString());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        //for (TableConstraintDefinition constraint : constraints){
        //
        //}
        sb.append(");");
        return sb.toString();
    }
}
