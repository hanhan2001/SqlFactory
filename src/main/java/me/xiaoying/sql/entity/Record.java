package me.xiaoying.sql.entity;

/**
 * Table Record
 */
public class Record {
    private final Table table;

    public Record(Table table) {
        this.table = table;
    }

    public Object get(int index) {
        if (index > this.table.getColumns().size())
            return null;

        for (int i = 0; i < this.table.getColumns().size(); i++) {
            if (i != index)
                continue;

            return this.table.getColumns().get(i).getValue();
        }
        return null;
    }

    public Object get(String column) {
        for (Column tableColumn : this.table.getColumns()) {
            if (!tableColumn.getName().equalsIgnoreCase(column))
                continue;

            return tableColumn.getValue();
        }
        return null;
    }

    public Table getTable() {
        return this.table;
    }
}