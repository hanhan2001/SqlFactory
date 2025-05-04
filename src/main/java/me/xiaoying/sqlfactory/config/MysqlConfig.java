package me.xiaoying.sqlfactory.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MysqlConfig extends Config {
    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final String database;

    public MysqlConfig(String username, String password, String host, int port, String database) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }
}