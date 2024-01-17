package me.xiaoying.sql.entity;

public enum ConditionType {
    EQUAL("="),
    NOT_EQUAL("<>"),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),
    LIKE("LIKE"),
    AND("AND"),
    OR("OR"),
    IS_NULL("NULL"),
    NOT("NOT"),
    NOT_NULL("NOT NULL");

    private final String key;

    ConditionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }


    @Override
    public String toString() {
        return this.key;
    }
}