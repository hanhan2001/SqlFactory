package me.xiaoying.sql.sentence;

import me.xiaoying.sql.entity.Column;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Sentence of sql
 */
public class Create extends Sentence {
    private final List<Column> columns;

    public Create(List<Column> columns, String table, String... tables) {
        super(table, tables);
        if (columns == null || columns.isEmpty())
            throw new RuntimeException(new InvalidParameterException("Column can't be empty when create table."));
        this.columns = columns;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS ");
        // merge tables
        stringBuilder.append(this.getTablesAsString());
        stringBuilder.append(" ");

        // merge columns
        stringBuilder.append("(");
        if (this.columns.isEmpty())
            throw new RuntimeException(new InvalidParameterException("Column can't be empty when create table."));

        for (int i = 0; i < this.columns.size(); i++) {
            Column column = this.columns.get(i);

            stringBuilder.append("`").append(column.getName()).append("` ");
            stringBuilder.append(column.getType()).append("(");
            stringBuilder.append(column.getSize()).append(")");

            if (column.canNull())
                stringBuilder.append(" DEFAULT NULL");
            else
                stringBuilder.append(" NOT NULL");

            if (i == this.columns.size() - 1)
                break;

            stringBuilder.append(", ");
        }
        stringBuilder.append(")");

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
    public Sentence condition(Condition condition) {
        return this;
    }
}