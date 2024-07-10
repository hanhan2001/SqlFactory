package me.xiaoying.sql.entity;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        Map<String, Integer> map = new LinkedHashMap<>();
        // calculate fields' width
        for (Record record : this.records) {
            for (Column column : this.getColumns()) {
                int length = record.get(column.getName()).toString().length();
                map.putIfAbsent(column.getName(), length);

                if (map.get(column.getName()) < length)
                    continue;

                map.put(column.getName(), length);
            }
        }

        // merge fields
        // calculate max width of fields
        int maxLength = 0;
        for (String s : map.keySet()) {
            if (map.get(s) < s.length())
                map.put(s, s.length());

            maxLength += map.get(s);
        }

        // merge first
        stringBuilder.append("╭");
        for (int i = 0; i < maxLength; i++)
            stringBuilder.append("─");
        for (int i = 0; i < 2 * this.columns.size() + this.columns.size() - 1; i++)
            stringBuilder.append("─");
        stringBuilder.append("╮");
        stringBuilder.append("\n");

        // merge fields
        for (Column column : this.columns) {
            String format;
            if (stringBuilder.toString().endsWith("│"))
                format = " %-" + map.get(column.getName()) + "s │";
            else
                format = "│ %-" + map.get(column.getName()) + "s │";

            stringBuilder.append(String.format(format, column.getName()));
        }
        stringBuilder.append("\n");

        // merge line
        stringBuilder.append("├");
        for (int i = 0; i < maxLength; i++)
            stringBuilder.append("─");
        for (int i = 0; i < 2 * this.columns.size() + this.columns.size() - 1; i++)
            stringBuilder.append("─");
        stringBuilder.append("┤");
        stringBuilder.append("\n");

        // merge records
        for (Record record : this.records) {
            for (Column column : this.columns) {
                String format;
                if (stringBuilder.toString().endsWith("│"))
                    format = " %-" + map.get(column.getName()) + "s │";
                else
                    format = "│ %-" + map.get(column.getName()) + "s │";

                stringBuilder.append(String.format(format, record.get(column.getName())));
            }
            stringBuilder.append("\n");
        }

        // merge end
        stringBuilder.append("╰");
        for (int i = 0; i < maxLength; i++)
            stringBuilder.append("─");
        for (int i = 0; i < 2 * this.columns.size() + this.columns.size() - 1; i++)
            stringBuilder.append("─");
        stringBuilder.append("╯");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}