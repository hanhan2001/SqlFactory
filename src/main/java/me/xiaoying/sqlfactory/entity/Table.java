package me.xiaoying.sqlfactory.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Table {
    private final String name;
    private String comment;

    public Table(String name) {
        this.name = name;
    }
}