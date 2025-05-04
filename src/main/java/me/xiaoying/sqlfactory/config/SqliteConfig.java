package me.xiaoying.sqlfactory.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.File;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SqliteConfig extends Config {
    private final File file;

    public SqliteConfig(File file) {
        this.file = file;
    }
}