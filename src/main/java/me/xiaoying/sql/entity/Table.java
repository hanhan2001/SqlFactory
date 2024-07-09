package me.xiaoying.sql.entity;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private final List<Record> records;
    private final List<Column> columns;

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

    public List<Column> getColumns() {
        return this.columns;
    }
}