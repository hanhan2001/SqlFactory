package me.xiaoying.sql.entity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Table
 */
public class Table {
    private String name;
    private Stack<Column> columns;
    private Stack<Record> records;

    public Table(String name) {
        this.name = name;
    }

    public Table(String name, Stack<Record> records) {
        this.name = name;
        this.records = records;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getColumn(String name) {
        for (Column column : this.columns) {
            if (!column.getName().equalsIgnoreCase(name))
                continue;

            return column;
        }
        return null;
    }

    public Stack<Column> getColumns() {
        return columns;
    }

    public void setColumn(Column column) {
        if (this.columns.contains(column))
            return;

        this.columns.add(column);
    }

    public void setColumns(Stack<Column> columns) {
        this.columns = columns;
    }

    public void removeColumn(Column column) {
        this.columns.remove(column);
    }

    public void removeColumn(String column) {
        Iterator<Column> iterator = this.columns.iterator();

        Column column1;
        while ((column1 = iterator.next()) != null) {
            if (!column1.getName().equalsIgnoreCase(column))
                continue;

            iterator.remove();
        }
    }

    public void removeColumns() {
        this.columns.clear();
    }

    public Stack<Record> getRecords() {
        return this.records;
    }

    public boolean contains(Column column) {
        return this.columns.contains(column);
    }
}