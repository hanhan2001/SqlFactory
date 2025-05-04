package me.xiaoying.sqlfactory.factory;

import com.mysql.cj.jdbc.Driver;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.config.MysqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlFactory extends SqlFactory {
    private final String host;
    private final int port;
    private final String database;

    private final String username;
    private final String password;

    public MysqlFactory(MysqlConfig config) {
        super(config.getMaxPoolSize(), config.getConnectionTimeout());

        this.host = config.getHost();
        this.port = config.getPort();
        this.database = config.getDatabase();

        this.username = config.getUsername();
        this.password = config.getPassword();

        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?allowMultiQueries=true", this.username, this.password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}