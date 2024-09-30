package me.xiaoying.sql.entity;

/**
 * Table Column
 */
public class Column {
    private Table table;
    private final String name;
    private final String type;
    private final int size;
    private boolean _null;

    /**
     * Constructor of Column
     *
     * @param name column's name
     * @param type column's type
     * @param size column's size
     */
    public Column(String name, String type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    /**
     * Constructor of Column
     *
     * @param table column's table
     * @param name column's name
     * @param type column's type
     * @param size column's size
     */
    public Column(Table table, String name, String type, int size) {
        this(name, type, size);
        this.table = table;
    }

    /**
     * Get name of column
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get falsehood name fo column
     *
     * @return String
     */
    public String getFalsehoodName() {
        if (!this.getName().contains("."))
            return this.getName();
        return this.getName().split("\\.")[1];
    }

    /**
     * Get type of column
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get size of column
     *
     * @return Integer
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Get colum's table
     *
     * @return Table
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Set columns default value can empty or not.
     *
     * @param value Boolean
     * @return Column
     */
    public Column setNull(boolean value) {
        this._null = value;
        return this;
    }

    /**
     * Determine if colum can be null
     *
     * @return Boolean
     */
    public boolean canNull() {
        return this._null;
    }
}