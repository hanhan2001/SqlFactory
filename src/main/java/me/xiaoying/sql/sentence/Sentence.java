package me.xiaoying.sql.sentence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sql sentence
 */
public abstract class Sentence {
    protected final List<String> tables = new ArrayList<>();
    protected final List<Condition> conditions = new ArrayList<>();

    public Sentence(String table, String... tables) {
        this.tables.add(table);

        if (tables.length == 0)
            return;

        this.tables.addAll(Arrays.asList(tables));
    }

    public List<String> getTables() {
        return this.tables;
    }

    public String getTablesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.tables.size(); i++) {
            stringBuilder.append("`").append(this.getTables().get(i)).append("`");

            if (i == this.tables.size() - 1)
                break;

            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    public Sentence condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public abstract String merge();

    @Override
    public String toString() {
        return this.merge();
    }
}