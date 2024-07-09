package me.xiaoying.sql.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Table Record
 */
public class Record {
    private final Table table;
    private final Map<Column, Object> values = new HashMap<>();

    public Record(Table table) {
        this.table = table;
    }

    public Object get(int index) {
        if (index > this.values.size())
            return null;

        int i = 0;
        for (Column column : this.values.keySet()) {
            if (i++ != index)
                continue;

            return this.values.get(column);
        }
        return null;
    }

    public Object get(String column) {
        for (Column column1 : this.values.keySet()) {
            if (!column1.getName().equalsIgnoreCase(column))
                continue;
            return this.values.get(column1);
        }
        return null;
    }

    public void set(Column column, Object value) {
        if (!this.table.containColumn(column.getName()))
            this.table.setColumn(column);

        this.values.put(column, value);
    }

    public Table getTable() {
        return this.table;
    }
}