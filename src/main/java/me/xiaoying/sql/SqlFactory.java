package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Condition;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.sentence.Sentence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * SqlFactory
 */
public abstract class SqlFactory {
    private final List<String> tables = new ArrayList<>();
    protected Sentence sentence;
    private Condition condition;

    /**
     * 获取链接
     *
     * @return Connection
     * @throws SQLException 异常
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * 获取表列
     *
     * @param table 表
     * @return Stack
     */
    public abstract Stack<Column> getColumns(String table);
    public abstract boolean containsTable(String table);
    public abstract long getTotalRecord(String table);

    /**
     * 设置操作表
     *
     * @param table Table
     * @return SqlFactory
     */
    public SqlFactory table(String... table) {
        this.tables.addAll(Arrays.asList(table));
        return this;
    }

    /**
     * 设置 语句 类型
     *
     * @param sentence Sentence
     * @return SqlFactory
     */
    public SqlFactory sentence(Sentence sentence) {
        this.sentence = sentence;
        return this;
    }

    public Sentence getSentence() {
        return this.sentence;
    }

    public SqlFactory condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public abstract Stack<Record> run();

    @Override
    public abstract String toString();

    /**
     * 关闭链接
     *
     * @param conn Connection
     * @param stmt PreparedStatement
     * @param rs ResultSet
     * @throws SQLException 异常
     */
    void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
}