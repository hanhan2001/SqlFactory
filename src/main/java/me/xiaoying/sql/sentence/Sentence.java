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

    /**
     * Constructor
     *
     * @param table table's name
     * @param tables multi tables' name
     */
    public Sentence(String table, String... tables) {
        this.tables.add(table);

        if (tables.length == 0)
            return;

        this.tables.addAll(Arrays.asList(tables));
    }

    /**
     * Get tables for this sentence
     *
     * @return ArrayList
     */
    public List<String> getTables() {
        return this.tables;
    }

    /**
     * Get tables as string for this sentence
     *
     * @return String
     */
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

    /**
     * Add new condition for this
     *
     * @param condition Condition[]
     * @return Condition
     */
    public Sentence condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    /**
     * Merge sentence to String
     *
     * @return String
     */
    public abstract String merge();

    @Override
    public String toString() {
        return this.merge();
    }
}