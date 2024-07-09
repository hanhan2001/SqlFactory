package me.xiaoying.sql.sentence;

/**
 * Sentence of
 */
public class Delete extends Sentence {
    public Delete(String table, String... tables) {
        super(table, tables);
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
        // merge tables
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