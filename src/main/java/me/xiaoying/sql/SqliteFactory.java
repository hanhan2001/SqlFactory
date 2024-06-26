package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.sentence.Select;
import me.xiaoying.sql.sentence.Sentence;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.Stack;

public class SqliteFactory extends SqlFactory {
    private final String path;
    private final String file;

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
            Connection conn = this.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++)
                columns.add(new Column(table + "." + rsmd.getColumnName(i), rsmd.getColumnTypeName(i), rsmd.getColumnDisplaySize(i)));

            this.closeAll(conn, stmt, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return columns;
    }

    @Override
    public boolean containsTable(String table) {
        try {
            Connection conn = this.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, table, null);
            if (rs.next())
                return true;

            this.closeAll(conn, null, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public long getTotalRecord(String table) {
        String sqlString = "SELECT COUNT(*) FROM " + table;
        long num = 0;
        try {
            Connection conn = this.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                num =  rs.getLong("COUNT(*)");

            super.closeAll(conn, stmt, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return num;
    }

    @Override
    public SqlFactory sentence(Sentence sentence) {
        this.sentence = sentence;
        return this;
    }

    public Stack<Record> run() {
        Stack<Record> records = new Stack<>();

        if (this.sentence == null)
            return null;

        try {
            Connection conn = this.getConnection();
            PreparedStatement stmt = conn.prepareStatement(this.toString());
            if (!(this.sentence instanceof Select)) {
                stmt.executeUpdate();
                this.closeAll(conn, stmt, null);
                return null;
            }

            ResultSet rs = stmt.executeQuery();
            Select select = (Select) this.sentence;
            while (rs.next()) {
                Record record = new Record();
                for (Column column : select.getColumns())
                    record.put(column, rs.getObject(column.getName()));

                records.add(record);
            }
            this.closeAll(conn, stmt, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    @Override
    public String toString() {
        if (this.sentence.getColumns().isEmpty())
            for (String table : this.sentence.getTables()) this.sentence.setColumns(this.getColumns(table));

        if (this.getCondition() == null)
            return this.sentence.merge();

        return this.sentence.merge() + " WHERE " + this.getCondition();
    }
}