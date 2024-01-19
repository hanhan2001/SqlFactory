package me.xiaoying.sql.sentence;

import me.xiaoying.sql.entity.Column;

/**
 * Sentence Create
 */
public class Create extends Sentence {
    public Create(String table, String... tables) {
        super(table, tables);
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE if NOT EXISTS (");
        for (int i = 0; i < this.tables.size(); i++) {
            stringBuilder.append(this.tables.get(i));

            if (i == this.tables.size() - 1)
                break;

            stringBuilder.append(", ");
        }
        stringBuilder.append(" ");

        for (int i = 0; i < this.getColumns().size(); i++) {
            Column column = this.columns.get(i);
            stringBuilder.append(column.getName()).append(" ")
                    .append(column.getType());
            if (column.getSize() != 0)
                stringBuilder.append("(").append(column.getSize()).append(")");
            if (i == this.getColumns().size() - 1)
                break;

            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}