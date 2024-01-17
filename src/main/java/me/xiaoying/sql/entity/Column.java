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

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public long getSize() {
        return this.size;
    }
}