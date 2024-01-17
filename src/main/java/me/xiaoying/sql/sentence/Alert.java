package me.xiaoying.sql.sentence;

/**
 * Sentence Alert
 */
public class Alert extends Sentence {
    public Alert(String table) {
        super(table);
    }

    public Alert drop(String column) {

        return this;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("ALERT TABLE ");



        return stringBuilder.toString();
    }
}