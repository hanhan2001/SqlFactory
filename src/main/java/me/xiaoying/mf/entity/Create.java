package me.xiaoying.mf.entity;

public class Create {
    private final String name;
    private final String type;
    private int length;

    public Create(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Create(String name, String type, int length) {
        this.name = name;
        this.type = type;
        this.length = length;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public int getLength() {
        return this.length;
    }
}