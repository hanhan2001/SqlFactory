package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.entity.Table;
import me.xiaoying.sql.sentence.Select;
import me.xiaoying.sql.sentence.Sentence;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory of Sql sentence for Sqlite
 */
public class SqliteFactory extends SqlFactory {
    private final File file;
    private Connection connection;

    /**
     * Constructor
     *
     * @param file sql server's file
     */
    public SqliteFactory(File file) {
        this.file = file;
    }

    /**
     * Get connection with sql server
     *
     * @return Connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        switch (this.connectionType) {
            default:
            case INTERMITTENT:
                return DriverManager.getConnection("jdbc:sqlite://" + this.file.getAbsolutePath());
            case MAINTAIN:
                if (this.connection == null || this.connection.isClosed())
                    this.connection = DriverManager.getConnection("jdbc:sqlite://" + this.file.getAbsolutePath());
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
                        Column column = new Column(metaData.getTableName(i + 1) + "." + metaData.getColumnName(i + 1), metaData.getColumnTypeName(i + 1), metaData.getColumnDisplaySize(i + 1), _null);
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