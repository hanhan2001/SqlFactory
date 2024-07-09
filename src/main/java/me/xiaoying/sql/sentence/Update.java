package me.xiaoying.sql.sentence;

import me.xiaoying.sql.entity.Column;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentence of sql
 */
public class Update extends Sentence {
    private Map<String, Object> sets = new HashMap<>();

    public Update(String table, String... tables) {
        super(table, tables);
    }

    public Update set(Column column, Object value) {
        return this.set(column.getName(), value);
    }

    public Update set(String key, Object value) {
        this.sets.put(key, value);
        return this;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        // merge tables
        stringBuilder.append(this.getTablesAsString());
        stringBuilder.append(" SET ");

        // merge set
        int time = 0;
        for (String s : this.sets.keySet()) {
            stringBuilder.append("`").append(s).append("`");
            stringBuilder.append(" = ");
            if (this.sets.get(s) instanceof String)
                stringBuilder.append("\"").append(this.sets.get(s)).append("\"");
            else
                stringBuilder.append(this.sets.get(s));

            if (time == this.sets.size() - 1)
                break;

            stringBuilder.append(", ");
            time++;
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

        return stringBuilder.toString();
    }
}