package me.xiaoying.sql;

import com.mysql.cj.jdbc.Driver;
import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.entity.Table;
import me.xiaoying.sql.sentence.Select;
import me.xiaoying.sql.sentence.Sentence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory of Sql sentence for Mysql
 */
public class MysqlFactory extends SqlFactory {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection = null;

    /**
     * Constructor
     *
     * @param host sql server's host
     * @param port sql server's port
     * @param database sql server's database
     * @param username sql server's username
     * @param password sql server's password
     */
    public MysqlFactory(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Get connection with sql server
     *
     * @return Connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new Driver());
        switch (this.connectionType) {
            default:
            case INTERMITTENT:
                return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            case MAINTAIN:
                if (this.connection == null || this.getConnection().isClosed())
                    this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
                return this.connection;
        }
    }

    /**
     * Run sentence
     *
     * @param sentences Sentence[]
     * @return ArrayList
     */
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

                    Record record = new Record(table);
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