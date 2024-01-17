package me.xiaoying.sql.sentence;

public class Delete extends Sentence {
    public Delete(String table, String... tables) {
        super(table, tables);
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");

        // table
        for (int i = 0; i < this.tables.size(); i++) {
            stringBuilder.append("`").append(this.tables.get(i)).append("`");

            if (i == this.tables.size() - 1)
                break;

            stringBuilder.append(", ");
        }

        return stringBuilder.toString();
    }
}