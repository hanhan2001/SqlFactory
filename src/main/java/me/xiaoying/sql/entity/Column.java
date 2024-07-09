package me.xiaoying.sql.entity;

/**
 * Table Column
 */
public class Column {
    private Table table;
    private final String name;
    private final String type;
    private final int size;
    private Object value;
    private String alias = null;
    private final boolean _null;

    public Column(String name, String type, int size, boolean canNull) {
        this.name = name;
        this.type = type;
        this.size = size;
        this._null = canNull;
    }

    public Column(Table table, String name, String type, int size, boolean canNull) {
        this(name, type, size, canNull);
        this.table = table;
    }

    public Column(Table table, String name, String type, int size, Object value) {
        this(table, name, type, size, true);
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public int getSize() {
        return this.size;
    }

    public Object getValue() {
        return this.value;
    }

    public Table getTable() {
        return this.table;
    }

    public Column setAlias(String name) {
        this.alias = name;
        return this;
    }

    public String getAlias() {
        return this.alias;
    }

    public boolean canNull() {
        return this._null;
    }
}