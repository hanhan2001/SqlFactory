package me.xiaoying.sql;

import me.xiaoying.sql.entity.Table;
import me.xiaoying.sql.sentence.Sentence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SqlFactory
 */
public abstract class SqlFactory {
    protected ConnectionType connectionType = ConnectionType.INTERMITTENT;

    /**
     * Set connection type for sql factory<br>
     * <dl>
     *     <dt>{@code INTERMITTENT}</dt>
     *     <dd>create connection to sql server and stop when sql sentence finish.</dd>
     *     <dt>{@code MAINTAIN}</dt>
     *     <dd>maintain connection to sql server.</dd>
     * </dl>
     *
     * @param type ConnectionType
     */
    public void setConnectionType(ConnectionType type) {
        this.connectionType = type;
    }

    /**
     * Get connection with sql server
     *
     * @return Connection
     */
    public abstract Connection getConnection() throws SQLException;

    public abstract List<Table> run(Sentence... sentences);

    public void closeAll(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();

        if (this.connectionType == ConnectionType.MAINTAIN)
            return;
        if (conn != null) conn.close();
    }

    public enum ConnectionType {
        INTERMITTENT,
        MAINTAIN,
    }
}