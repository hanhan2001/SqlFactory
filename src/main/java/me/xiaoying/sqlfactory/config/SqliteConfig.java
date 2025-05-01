package me.xiaoying.sqlfactory.config;

import java.io.File;

public class SqliteConfig extends Config {
    private final File file;

    public SqliteConfig(File file) {
        this.file = file;
    }
}