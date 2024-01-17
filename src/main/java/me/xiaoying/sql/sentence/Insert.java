package me.xiaoying.sql.sentence;

import java.util.Stack;

/**
 * Sentence Insert
 */
public class Insert extends Sentence {
    Stack<String> insert = new Stack<>();

    public Insert(String table, String... tables) {
        super(table, tables);
    }

    public Insert insert(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append("\"").append(strings[i]).append("\"");
            if (i == strings.length - 1)
                break;

            stringBuilder.append(", ");
        }

        this.insert.add(stringBuilder.toString());
        return this;
    }

    @Override
    public String merge() {
        StringBuilder string = new StringBuilder("INSERT INTO ");

        StringBuilder table = new StringBuilder();
        for (int i = 0; i < this.tables.size(); i++) {
            table.append("`").append(this.tables.get(i)).append("`");

            if (i == this.tables.size() - 1)
                break;

            table.append(", ");
        }
        string.append(table).append(" VALUES ");

        for (int i = 0; i < this.insert.size(); i++) {
            string.append("(").append(this.insert.get(i)).append(")");

            if (i == this.insert.size() - 1)
                break;

            string.append(", ");
        }
        return string.toString();
    }
}