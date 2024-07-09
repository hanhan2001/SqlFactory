package me.xiaoying.sql.sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentence of sql
 */
public class Select extends Sentence {
    private List<String> columns;

    public Select(String table, String... tables) {
        super(table, tables);
    }

    public Select(List<String> columns, String table, String... tables) {
        super(table, tables);
        this.columns = columns;
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public List<String> getColumnsOnlyName() {
        List<String> list = new ArrayList<>();
        for (String column : this.columns) {
            if (!column.contains(".")) {
                list.add(column);
                continue;
            }

            String[] split = column.split("\\.");
            list.add(split[split.length - 1]);
        }
        return list;
    }

    public boolean containsColumn(String table, String column) {
        for (String s : this.columns) {
            if (s.contains(".")) {
                String[] split = s.split("\\.");
                if (split[0].equalsIgnoreCase(table) && split[1].equalsIgnoreCase(column))
                    return true;
            } else if (s.equalsIgnoreCase(column))
                return false;
        }
        return true;
    }

    @Override
    public String merge() {
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