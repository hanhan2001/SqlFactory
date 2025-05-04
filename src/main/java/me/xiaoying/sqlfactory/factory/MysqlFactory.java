package me.xiaoying.sqlfactory.factory;

import com.mysql.cj.jdbc.Driver;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.config.MysqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlFactory extends SqlFactory {
    private final MysqlConfig config;

    public MysqlFactory(MysqlConfig config) {
        super(config.getMaxPoolSize(), config.getConnectionTimeout());

        this.config = config;

        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + this.config.getHost() + ":" + this.config.getPort() + "/" + this.config.getDatabase() + "?allowMultiQueries=true", this.config.getUsername(), this.config.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}