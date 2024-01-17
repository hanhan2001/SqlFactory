package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Record;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.Stack;

public class SqliteFactory extends SqlFactory {
    String path;
    String file;

    public SqliteFactory(String path, String file) {
        this.path = path;
        this.file = file;
    }

    @Override
    public Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        return DriverManager.getConnection(this.path + ":" + this.file);
    }

    @Override
    public Stack<Column> getColumns(String table) {
        Stack<Column> columns = new Stack<>();
        // 1 = 0 避免查询实际存在数据，减少查询速度
        String sqlString = "SELECT * FROM " + table + " WHERE 1 = 0";
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement(sqlString);
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++)
                columns.add(new Column(rsmd.getCatalogName(i), rsmd.getColumnTypeName(i), rsmd.getColumnDisplaySize(i)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return columns;
    }
    @Override
    public Stack<Record> run() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}