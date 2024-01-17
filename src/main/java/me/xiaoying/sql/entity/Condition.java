package me.xiaoying.sql.entity;

public class Condition {
    private final String key;
    private final String value;
    private final ConditionType conditionType;
    private String string;
    private ConditionType type;

    public Condition(String key, String value, ConditionType type) {
        this.key = key;
        this.value = value;
        this.conditionType = type;
    }

    public Condition condition(Condition condition) {
        String string = condition.toString();

        if (condition.getType() != null)
            this.string = " " + condition.getType().getKey() + " ";
        else
            this.string = " AND ";

        if (string.contains("AND") || string.contains("OR"))
            string = "(" + string + ")";

        this.string = this.string + string;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public Condition type(ConditionType type) {
        this.type = type;
        return this;
    }

    public ConditionType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("`").append(this.key).append("`");
        stringBuilder.append(" ").append(this.conditionType).append(" ");
        stringBuilder.append("\"").append(this.value).append("\"");

        if (this.string != null)
            stringBuilder.append(this.string);
        return stringBuilder.toString();
    }
}