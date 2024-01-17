package me.xiaoying.sql.sentence;

import me.xiaoying.sql.entity.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Sentence
 */
public abstract class Sentence {
    List<String> tables = new ArrayList<>();
    Stack<Column> columns = new Stack<>();

    public Sentence(String table, String... tables) {
        this.tables.add(table);
        Collections.addAll(this.tables, tables);
    }

    public List<String> getTables() {
        return this.tables;
    }

    public Stack<Column> getColumns() {
        return this.columns;
    }

    public void setColumns(Stack<Column> columns) {
        for (Column column : columns) {
            if (this.columns.contains(column))
                continue;

            this.columns.add(column);
        }
    }

    public abstract String merge();
}