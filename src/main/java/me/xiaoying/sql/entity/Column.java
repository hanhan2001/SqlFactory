package me.xiaoying.sql.entity;

/**
 * Column 列
 */
public class Column {
    String name;
    String type;
    long size;

    public Column(String name, String type, long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    /**
     * Only get column's name<br>
     * For example: column
     *
     * @return column's name
     */
    public String getName() {
        String name;
        if (this.name.contains(".")) {
            String[] split = this.name.split("\\.");
            name = split[split.length - 1];
        } else
            name = this.name;
        return name;
    }

    /**
     * Get column full.<br>
     * For example: table.column
     *
     * @return column's full name
     */
    public String getFullName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public long getSize() {
        return this.size;
    }
}