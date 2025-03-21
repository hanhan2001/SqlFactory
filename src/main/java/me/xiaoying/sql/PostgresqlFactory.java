package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.entity.Table;
import me.xiaoying.sql.sentence.Select;
import me.xiaoying.sql.sentence.Sentence;
import org.postgresql.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlFactory extends SqlFactory {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection = null;

    public PostgresqlFactory(String password, String username, String database, int port, String host) {
        this.password = password;
        this.username = username;
        this.database = database;
        this.port = port;
        this.host = host;
    }

    @Override
    public Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new Driver());
        switch (this.connectionType) {
            default:
            case INTERMITTENT:
                return DriverManager.getConnection("jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            case MAINTAIN:
                if (this.connection == null || this.getConnection().isClosed())
                    this.connection = DriverManager.getConnection("jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
                return this.connection;
        }
    }

    @Override
    public List<Table> run(Sentence... sentences) {
        List<Table> tables = new ArrayList<>();
        try {
            for (Sentence sentence : sentences) {
                Connection conn = this.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sentence.merge());
                if (!(sentence instanceof Select)) {
                    stmt.executeUpdate();
                    this.closeAll(conn, stmt, null);
                    continue;
                }

                ResultSet rs = stmt.executeQuery();
                Table table = new Table("Select");
                while (rs.next()) {
                    ResultSetMetaData metaData = rs.getMetaData();

                    me.xiaoying.sql.entity.Record record = new Record(table);
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        boolean _null = metaData.isNullable(i + 1) != ResultSetMetaData.columnNoNulls;
                        Column column = new Column(metaData.getTableName(i + 1) + "." + metaData.getColumnName(i + 1), metaData.getColumnTypeName(i + 1), metaData.getColumnDisplaySize(i + 1)).setNull(_null);
                        record.set(column, rs.getObject(i + 1));
                    }
                    table.setRecord(record);
                }

                tables.add(table);
                this.closeAll(conn, stmt, rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
}