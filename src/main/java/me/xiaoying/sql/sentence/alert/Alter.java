package me.xiaoying.sql.sentence.alert;

import me.xiaoying.sql.sentence.Sentence;

public class Alter extends Sentence {
    private AlterSentence alterSentence;

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
        this.alterSentence = sentence;
        return this;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("ALERT TABLE ").append(this.getTablesAsString());
        if (this.alterSentence  != null)
            stringBuilder.append(" ").append(this.alterSentence.merge());
        return stringBuilder.toString();
    }
}