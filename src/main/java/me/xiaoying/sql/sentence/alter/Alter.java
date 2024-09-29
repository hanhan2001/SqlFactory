package me.xiaoying.sql.sentence.alter;

import me.xiaoying.sql.sentence.Sentence;

public class Alter extends Sentence {
    private AlterSentence sentence;

    /**
     * Constructor
     *
     * @param table  table's name
     * @param tables multi tables' name
     */
    public Alter(String table, String... tables) {
        super(table, tables);
    }

    public Alter sentence(AlterSentence sentence) {
        this.sentence = sentence;
        return this;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("ALTER TABLE ");

        // merge tables
        stringBuilder.append(this.getTablesAsString());

        if (this.sentence == null)
            return stringBuilder.toString();

        stringBuilder.append(" ");
        stringBuilder.append(this.sentence.merge());
        return stringBuilder.toString();
    }
}