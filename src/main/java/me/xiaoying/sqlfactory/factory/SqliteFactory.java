package me.xiaoying.sqlfactory.factory;

import me.xiaoying.sqlfactory.SqlFactory;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteFactory extends SqlFactory {
    private final File file;

    public SqliteFactory(File file, int maxPoolSize) {
        super(maxPoolSize);

        if (file == null)
            throw new IllegalArgumentException("file is null");

        this.file = file;

        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}