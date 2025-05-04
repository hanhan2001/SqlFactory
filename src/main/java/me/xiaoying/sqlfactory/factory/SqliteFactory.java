package me.xiaoying.sqlfactory.factory;

import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.config.SqliteConfig;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteFactory extends SqlFactory {
    private final SqliteConfig config;

    public SqliteFactory(SqliteConfig config) {
        super(config.getMaxPoolSize(), config.getConnectionTimeout());

        this.config = config;

        if (this.config.getFile() == null)
            throw new IllegalArgumentException("file is null");

        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + this.config.getFile().getAbsolutePath());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}