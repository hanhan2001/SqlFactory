package me.xiaoying.sql.sentence;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentence Update
 */
public class Update extends Sentence {
    Map<String, String> sets = new HashMap<>();

    public Update(String table, String... tables) {
        super(table, tables);
    }

    public Update set(String key, String value) {
        this.sets.put(key, value);
        return this;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");

        // table
        for (int i = 0; i < this.tables.size(); i++) {
            stringBuilder.append(this.tables.get(i));

            if (i == this.tables.size() - 1)
                break;

            stringBuilder.append(", ");
        }

        stringBuilder.append(" SET ");

        int time = 0;
        for (String s : this.sets.keySet()) {

            stringBuilder.append("`").append(s).append("`").append(" = ").append("\"").append(this.sets.get(s)).append("\"");

            if (time == this.sets.size() - 1)
                break;

            stringBuilder.append(", ");
            time++;
        }
        return stringBuilder.toString();
    }
}