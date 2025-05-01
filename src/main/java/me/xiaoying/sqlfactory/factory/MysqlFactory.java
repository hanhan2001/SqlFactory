package me.xiaoying.sqlfactory.factory;

import com.mysql.cj.jdbc.Driver;
import me.xiaoying.sqlfactory.SqlFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlFactory extends SqlFactory {
    private final String host;
    private final int port;
    private final String database;

    private final String username;
    private final String password;

    public MysqlFactory(String host, int port, String database, String username, String password, int maxPoolSize) {
        super(maxPoolSize);

        this.host = host;
        this.port = port;
        this.database = database;

        this.username = username;
        this.password = password;

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