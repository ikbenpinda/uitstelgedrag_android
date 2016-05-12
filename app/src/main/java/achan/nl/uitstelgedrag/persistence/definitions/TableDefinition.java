package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Created by Etienne on 3-4-2016.
 */
public class TableDefinition {
    public String name;
    public ColumnDefinition[] columns;

    public TableDefinition(String name, ColumnDefinition... columns) {
        this.name = name;
        this.columns = columns;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append("(");
        for (ColumnDefinition column : columns) {
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
