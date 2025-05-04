package me.xiaoying.sqlfactory.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostgreSqlConfig extends Config {
    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final String database;

    public PostgreSqlConfig(String username, String password, String host, int port, String database) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }
}