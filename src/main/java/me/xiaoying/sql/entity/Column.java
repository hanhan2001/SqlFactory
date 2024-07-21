package me.xiaoying.sql.entity;

/**
 * Table Column
 */
public class Column {
    private Table table;
    private final String name;
    private final String type;
    private final int size;
    private final boolean _null;

    /**
     * Constructor of Column
     *
     * @param name column's name
     * @param type column's type
     * @param size column's size
     * @param canNull columns can be null or not
     */
    public Column(String name, String type, int size, boolean canNull) {
        this.name = name;
        this.type = type;
        this.size = size;
        this._null = canNull;
    }

    /**
     * Constructor of Column
     *
     * @param table column's table
     * @param name column's name
     * @param type column's type
     * @param size column's size
     * @param canNull columns can be null or not
     */
    public Column(Table table, String name, String type, int size, boolean canNull) {
        this(name, type, size, canNull);
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
     * Determine if colum can be null
     *
     * @return Boolean
     */
    public boolean canNull() {
        return this._null;
    }
}