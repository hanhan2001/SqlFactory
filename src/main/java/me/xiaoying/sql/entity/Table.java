package me.xiaoying.sql.entity;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private List<Record> records = new ArrayList<>();
    private List<Column> columns = new ArrayList<>();

    public Table(String name) {
        this.name = name;
    }

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

    public List<Record> getRecords() {
        return this.records;
    }

    public void setRecord(Record record) {
        this.records.add(record);
    }

    public void setRecords(List<Record> records) {
        if (records == null) {
            this.records.clear();
            return;
        }

        this.records = records;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumn(Column column) {
        this.columns.add(column);
    }

    public void setColumns(List<Column> columns) {
        if (columns == null) {
            this.columns.clear();
            return;
        }

        this.columns = columns;
    }

    public boolean containColumn(String column) {
        for (Column column1 : this.columns) {
            if (!column1.getName().equalsIgnoreCase(column))
                continue;

            return true;
        }
        return false;
    }
}