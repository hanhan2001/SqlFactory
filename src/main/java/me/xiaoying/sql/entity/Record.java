package me.xiaoying.sql.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Record {
    Map<Column, Object> values = new HashMap<>();

    public Record put(Column column, Object value) {
        this.values.put(column, value);
        return this;
    }

    public Object get(Column column) {
        return this.values.get(column);
    }

    public Object get(String column) {
        for (Column col : this.values.keySet()) {
            if (!col.getName().equalsIgnoreCase(column))
                continue;

            return this.values.get(col);
        }
        return null;
    }

    public List<Column> getColumns() {
        return new ArrayList<>(this.values.keySet());
    }
}