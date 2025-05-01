package me.xiaoying.sqlfactory.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MysqlConfig extends Config {
    private String username;
    private String password;
    private String host;
    private int port;
    private String database;

    public MysqlConfig(String username, String password, String host, int port, String database) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }
}