package me.xiaoying.sql.sentence;

/**
 * Sentence of sql
 */
public class Drop extends Sentence {
    /**
     * Constructor
     *
     * @param table  table's name
     * @param tables multi tables' name
     */
    public Drop(String table, String... tables) {
        super(table, tables);
    }

    /**
     * Merge sentence to String
     *
     * @return String
     */
    @Override
    public String merge() {
        return "DROP TABLE " + this.getTablesAsString();
    }
}