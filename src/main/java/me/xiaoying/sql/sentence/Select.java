package me.xiaoying.sql.sentence;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Sentence of sql
 */
public class Select extends Sentence {
    private List<String> columns;

    /**
     * Constructor
     *
     * @param table table's name
     * @param tables multi tables' name
     */
    public Select(String table, String... tables) {
        super(table, tables);
    }

    /**
     * Constructor
     *
     * @param columns List(String)
     * @param table table's name
     * @param tables multi tables' name
     */
    public Select(List<String> columns, String table, String... tables) {
        super(table, tables);
        this.columns = columns;
    }

    /**
     * Get columns for this sentence
     *
     * @return ArrayList
     */
    public List<String> getColumns() {
        return this.columns;
    }

    /**
     * Merge sentence to String
     *
     * @return String
     */
    @Override
    public String merge() {
        if (this.columns.isEmpty())
            throw new RuntimeException(new InvalidParameterException("Column can't be empty when select record."));

        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        // merge columns
        for (int i = 0; i < this.columns.size(); i++) {
            String c = this.columns.get(i);
            if (!c.contains("."))
                c = "`" + c + "`";
            else {
                String[] split = c.split("\\.");
                c = split[0] + ".`" + split[1] + "`";
            }
            stringBuilder.append(c);
            if (i == this.columns.size() - 1)
                break;

            stringBuilder.append(", ");
        }

        // merge tables
        stringBuilder.append(" FROM ");
        stringBuilder.append(this.getTablesAsString()).append(" ");

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
        return stringBuilder.toString();
    }
}