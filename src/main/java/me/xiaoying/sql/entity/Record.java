package me.xiaoying.sql.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Table Record
 */
public class Record {
    private final Table table;
    private final Map<Column, Object> values = new HashMap<>();

    /**
     * Constructor
     *
     * @param table record's table
     */
    public Record(Table table) {
        this.table = table;
    }

    /**
     * Get value by serial number
     *
     * @param index Integer
     * @return Object
     */
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

    /**
     * Get value by column's name
     *
     * @param column Column
     * @return Object
     */
    public Object get(String column) {
        for (Column column1 : this.values.keySet()) {
            if (column1.getName().contains(".")) {
                String name = column1.getName().replace("`", "");

                if (!column.contains("."))
                    name = name.split("\\.")[1];

                if (!name.equalsIgnoreCase(column))
                    continue;

                return this.values.get(column1);
            }

            String name = column1.getName().replace("`", "");
            if (!name.equalsIgnoreCase(column))
                continue;

            return this.values.get(column1);
        }
        return null;
    }

    /**
     * Set value of column
     *
     * @param column Column
     * @param value Object
     */
    public void set(Column column, Object value) {
        if (!this.table.containColumn(column.getName()))
            this.table.setColumn(column);

        this.values.put(column, value);
    }

    /**
     * Get record's table
     *
     * @return Table
     */
    public Table getTable() {
        return this.table;
    }
}