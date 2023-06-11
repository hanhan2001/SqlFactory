package me.xiaoying.mf.entity;

public class Condition {
    private final String key;
    private final String string;
    private final String type;

    public Condition(String key, String string, String type) {
        this.key = key;
        this.string = string;
        this.type = type;
    }

    public String getKey() {
        return this.key;
    }

    public String getString() {
        return this.string;
    }

    public String getType() {
        return this.type;
    }
}
