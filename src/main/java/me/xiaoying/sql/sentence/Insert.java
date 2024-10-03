package me.xiaoying.sql.sentence;

import java.util.Stack;

/**
 * Sentence of sql
 */
public class Insert extends Sentence {
    private Stack<String> inserts = new Stack<>();

    /**
     * Constructor
     *
     * @param table table's name
     * @param tables multi tables' name
     */
    public Insert(String table, String... tables) {
        super(table, tables);
    }

    public void insert(String[] values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            stringBuilder.append("\"").append(values[i]).append("\"");

            if (i == values.length - 1)
                break;

            stringBuilder.append(", ");
        }

        this.inserts.add(stringBuilder.toString());
    }

    /**
     * Add insert value
     *
     * @param values Object[]
     */
    public void insert(Object... values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            Object object = values[i];
            if (object instanceof String)
                stringBuilder.append("\"").append(object).append("\"");
            else
                stringBuilder.append(object);

            if (i == values.length - 1)
                break;

            stringBuilder.append(", ");
        }

        this.inserts.add(stringBuilder.toString());
    }

    /**
     * Merge sentence to String
     *
     * @return String
     */
    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        // merge tables
        stringBuilder.append(this.getTablesAsString());
        stringBuilder.append(" VALUES ");

        for (int i = 0; i < this.inserts.size(); i++) {
            stringBuilder.append("(").append(this.inserts.get(i)).append(")");

            if (i == this.inserts.size() - 1)
                break;

            stringBuilder.append(", ");
        }

        // merge conditions
        if (this.conditions.isEmpty())
            return stringBuilder.toString();

        stringBuilder.append(" WHERE");
        for (int i = 0; i < this.conditions.size(); i++) {
            stringBuilder.append(" ");

            Condition condition = this.conditions.get(i);
            if (i == 0)
                stringBuilder.append(condition.merge());
            else
                stringBuilder.append(condition.getConnctionType()).append(" ").append(condition.merge());
        }

        // create can't use condition, so I haven't made it
        return stringBuilder.toString();
    }

    /**
     * This method is not supported in this sentence
     *
     * @param condition Condition
     * @return Sentence
     */
    @Override
    @Deprecated
    public Insert condition(Condition condition) {
        return this;
    }
}