package me.xiaoying.sql.sentence;

import me.xiaoying.sql.entity.Column;

import java.util.Collections;

/**
 * Sentence Select
 */
public class Select extends Sentence {
    public Select(String table, String... tables) {
        super(table, tables);
    }

    public Select column(Column... column) {
        Collections.addAll(this.columns, column);
        return this;
    }

    @Override
    public String merge() {
        String string = "SELECT ";

        StringBuilder column = new StringBuilder();

        if (this.columns.size() != 0) {
            for (int i = 0; i < this.columns.size(); i++) {
                String[] split = this.columns.get(i).getName().split("\\.");
                column.append(split[0]).append(".`").append(split[1]).append("`");

                if (i == this.columns.size() - 1)
                    break;

                column.append(", ");
            }
        }

        string = string + column + " FROM " + this.tables.get(0);
        return string;
    }
}