package me.xiaoying.sql.entity;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private List<Record> records = new ArrayList<>();
    private List<Column> columns = new ArrayList<>();

    /**
     * Constructor
     *
     * @param name table's name
     */
    public Table(String name) {
        this.name = name;
    }

    /**
     * Constructor
     *
     * @param name table's name
     * @param records table's records
     * @param columns table's columns
     */
    public Table(String name, List<Record> records, List<Column> columns) {
        this.name = name;

        if (records == null)
            this.records = new ArrayList<>();
        else
            this.records = records;

        if (columns == null)
            this.columns = new ArrayList<>();
        else
            this.columns = columns;
    }

    /**
     * Get records
     *
     * @return ArrayList
     */
    public List<Record> getRecords() {
        return this.records;
    }

    /**
     * Add record
     *
     * @param record Record
     */
    public void setRecord(Record record) {
        this.records.add(record);
    }

    /**
     * Add records
     *
     * @param records List
     */
    public void setRecords(List<Record> records) {
        if (records == null) {
            this.records.clear();
            return;
        }

        this.records = records;
    }

    /**
     * Get columns
     *
     * @return ArrayList
     */
    public List<Column> getColumns() {
        return this.columns;
    }

    /**
     * Add columns
     *
     * @param column Column
     */
    public void setColumn(Column column) {
        this.columns.add(column);
    }

    /**
     * Add columns
     *
     * @param columns List
     */
    public void setColumns(List<Column> columns) {
        if (columns == null) {
            this.columns.clear();
            return;
        }

        this.columns = columns;
    }

    /**
     * Determine if a column exists
     *
     * @param column String
     * @return Boolean
     */
    public boolean containColumn(String column) {
        for (Column column1 : this.columns) {
            if (!column1.getName().equalsIgnoreCase(column))
                continue;

            return true;
        }
        return false;
    }
}