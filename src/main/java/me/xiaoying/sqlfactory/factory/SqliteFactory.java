package me.xiaoying.sqlfactory.factory;

import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.config.SqliteConfig;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteFactory extends SqlFactory {
    private final SqliteConfig config;
    private Connection connection;

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

    /**
     * Sqlite 数据库不支持多连接，所以需要手动实现单连接
     *
     * @return Connection
     * @throws SQLException SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed())
            this.connection = this.createConnection();

        return this.connection;
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