package me.xiaoying.sqlfactory.factory;

import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.config.PostgreSqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSqlFactory extends SqlFactory {
    private PostgreSqlConfig config;

    public PostgreSqlFactory(PostgreSqlConfig config) {
        super(config.getMaxPoolSize(), config.getConnectionTimeout());
    }

    @Override
    protected Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://" + this.config.getHost() + ":" + this.config.getPort() + "/" + this.config.getDatabase() + "?allowMultiQueries=true", this.config.getUsername(), this.config.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}