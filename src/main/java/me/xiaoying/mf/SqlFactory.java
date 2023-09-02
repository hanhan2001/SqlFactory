package me.xiaoying.mf;

import com.mysql.cj.jdbc.Driver;
import me.xiaoying.mf.entity.Condition;
import me.xiaoying.mf.entity.Create;
import me.xiaoying.mf.entity.Set;
import me.xiaoying.mf.exception.UnknownDatabaseType;
import me.xiaoying.mf.utils.ExceptionUtil;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class SqlFactory {
    private final String host;
    private int port;
    private String database;
    private String user;
    private String pass;
    private SqlType type;
    private final Stack<String> cols = new Stack<>();
    private final Stack<String> tables = new Stack<>();
    private final Stack<String> inserts = new Stack<>();
    private final Stack<Condition> conditions = new Stack<>();
    private final Stack<Set> sets = new Stack<>();
    private final Stack<Create> create = new Stack<>();
    private String group;
    private String file;

    /**
     * 构建 Mysql
     *
     * @param host 主机地址
     * @param port 端口
     * @param database 数据库
     * @param user 用户
     * @param pass 密码
     */
    public SqlFactory(String host, int port, String database, String user, String pass) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.pass = pass;
    }


    /**
     * 构建 Sqlite
     *
     * @param host 主机地址
     * @param file 文件
     */
    public SqlFactory(String host, String file) {
        this.host = host;
        this.file = file;
    }

    /**
     * 链接 Mysql
     *
     * @return Connection
     */
    public Connection getConnection() {
        String type;

        try {
            type = this.host.split(":")[1];
            switch (type.toUpperCase()) {
                case "MYSQL": {
                    DriverManager.registerDriver(new Driver());
                    return DriverManager.getConnection(this.host + ":" + this.port + "/" + this.database, this.user, this.pass);
                }
                case "SQLITE": {
                    DriverManager.registerDriver(new JDBC());
                    return DriverManager.getConnection(this.host + ":" + this.file);
                }
                default:
                    ExceptionUtil.throwException(new UnknownDatabaseType("SqlFactory just use to Mysql or Sqlite"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 语句类型
     *
     * @param type 类型
     */
    public SqlFactory type(SqlType type) {
        this.type = type;
        return this;
    }

    /**
     * 来源表
     *
     * @param tables 表
     */
    public SqlFactory table(String... tables) {
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    /**
     * 移除来源表
     *
     * @param table 表名
     * @return 新的Factory
     */
    public SqlFactory removeTable(String table) {
        if (!this.tables.contains(table))
            return this;

        this.tables.remove(table);
        return this;
    }

    /**
     * 来源列
     *
     * @param col 列
     * @return 新的Factory
     */
    public SqlFactory cols(String... col) {
        this.cols.addAll(Arrays.asList(col));
        return this;
    }

    /**
     * 来源列
     *
     * @param col 列
     * @return 新的Factory
     */
    public SqlFactory removeCols(String col) {
        if (!this.cols.contains(col))
            return this;

        this.cols.remove(col);
        return this;
    }

    /**
     * 执行sql语句
     */
    public Map<String, List<Object>> run() {
        Connection conn = this.getConnection();

        try {
            PreparedStatement stmt = conn.prepareStatement(this.getSql());
            if (this.type != SqlType.SELECT) {
                stmt.executeUpdate();
                return null;
            }

            ResultSet rs = stmt.executeQuery();
            HashMap<String, List<Object>> map = new HashMap<>();
            List<Object> list;
            while (rs.next()) {
                for (String s : this.cols) {
                    if (map.get(s) == null)
                        list = new ArrayList<>();
                    else
                        list = map.get(s);

                    list.add(rs.getObject(s.replace("`", "")));
                    map.put(s, list);
                }
            }

            this.closeAll(conn, stmt, null);
            return map;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 清除配置
     */
    public SqlFactory clear() {
        this.type = null;
        this.cols.clear();
        this.tables.clear();
        this.conditions.clear();
        this.sets.clear();
        this.inserts.clear();
        this.create.clear();
        return this;
    }

    /**
     * 创建字段
     *
     * @param key 字段名
     * @param type 字段类型
     * @param length 字段长度
     * @return 新的Factory
     */
    public SqlFactory create(String key, String type, int length) {
        this.create.add(new Create(key, type, length));
        return this;
    }
    /**
     * 创建字段
     *
     * @param key 字段名
     * @return 新的Factory
     */
    public SqlFactory removeCreate(String key) {
        this.create.removeIf(create -> create.getName().equalsIgnoreCase(key));
        return this;
    }

    /**
     * 分组字段
     *
     * @param group 分组字段
     * @return 新的Factory
     */
    public SqlFactory group(String group) {
        this.group = group;
        return this;
    }

    /**
     * 移除分组字段
     *
     * @return 新的Factory
     */
    public SqlFactory removeGroup() {
        this.group = null;
        return this;
    }

    /**
     * 判断条件
     *
     * @param key 对象
     * @param value 值
     * @return 新的Factory
     */
    public SqlFactory condition(String key, String value) {
        this.conditions.add(new Condition(key, value, "AND"));
        return this;
    }

    /**
     * 判断条件
     *
     * @param key 对象
     * @param value 值
     * @param type 判断类型
     * <p>
     * type(类型):
     * <ul>
     *             <li>OR</li>
     *             <li>AND</li>
     * </ul>
     * @return 新的Factory
     */
    public SqlFactory condition(String key, String value, String type) {
        if (!type.equalsIgnoreCase("OR") && !type.equalsIgnoreCase("AND")) {
            try {
                throw new Exception("Unknown type, you can set 'OR' or 'AND'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.conditions.add(new Condition(key, value, type));
        return this;
    }

    /**
     * 移除判断条件
     *
     * @param key 对象
     * @return 新的Factory
     */
    public SqlFactory removeCondition(String key) {
        this.conditions.removeIf(condition -> condition.getKey().equalsIgnoreCase(key));
        return this;
    }

    /**
     * 移除判断条件
     *
     * @param key 对象
     * @param value 值
     * @return 新的Factory
     */
    public SqlFactory removeCondition(String key, String value) {
        this.conditions.removeIf(condition -> condition.getKey().equalsIgnoreCase(key) && condition.getString().equalsIgnoreCase(value));
        return this;
    }

    /**
     * 移除判断条件
     *
     * @param key 对象
     * @param value 值
     * @param type 判断类型
     * @return 新的Factory
     */
    public SqlFactory removeCondition(String key, String value, String type) {
        this.conditions.removeIf(condition -> condition.getKey().equalsIgnoreCase(key) && condition.getString().equalsIgnoreCase(value) && condition.getType().equalsIgnoreCase(type));
        return this;
    }

    /**
     * 写入新数据
     *
     * @param key 列名称
     * @return 新的Factory
     */
    public SqlFactory insert(String... key) {
        this.inserts.addAll(Arrays.asList(key));
        return this;
    }

    /**
     * 移除写入新数据
     *
     * @param key 列名称
     * @return 新的Factory
     */
    public SqlFactory removeInsert(String key) {
        this.inserts.remove(key);
        return this;
    }

    /**
     * 更新字段
     *
     * @param key 键值
     * @param value 更新值
     * @return 新的Factory
     */
    public SqlFactory set(String key, String value) {
        sets.add(new Set(key, value));
        return this;
    }


    /**
     * 移除更新字段
     *
     * @param key 键值
     * @return 新的Factory
     */
    public SqlFactory removeSet(String key) {
        this.sets.removeIf(set -> set.getKey().equalsIgnoreCase(key));
        return this;
    }

    /**
     * 拼接Sql语句
     *
     * @return String
     */
    public String getSql() {
        if (this.type == null) {
            try {
                throw new Exception("Unset sql type, need to code factory.type(SqlType)");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        switch (this.type) {
            case SELECT:
                return this.selectSql();
            case UPDATE:
                return this.updateSql();
            case DELETE:
                return this.deleteSql();
            case INSERT:
                return this.insertSql();
            case CREATE:
                return this.createSql();
        }
        return null;
    }

    /**
     * sql语句-select
     *
     * @return String
     */
    private String selectSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");

        if (this.cols.size() == 0)
            stringBuilder.append("*");
        else
            stringBuilder.append(this.colMontage());

        stringBuilder.append(" FROM ")
                .append(this.tableMontage());

        if (this.conditions.size() != 0)
            stringBuilder.append(" WHERE ").append(this.conditionMontage());

        if (this.group != null)
            stringBuilder
                    .append(" GROUP BY ")
                    .append(this.group);

        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    /**
     * sql语句-create
     *
     * @return String
     */
    private String createSql() {
        return "CREATE TABLE if NOT EXISTS " + this.tableMontage() + " (" + this.createMontage() + ");";
    }

    /**
     * sql语句-update
     *
     * @return String
     */
    private String updateSql() {
        if (this.conditions.size() == 0)
            return "UPDATE " + this.tableMontage() + " SET " + this.setMontage() + ";";
        else
            return "UPDATE " + this.tableMontage() + " SET " + this.setMontage() + " WHERE " + this.conditionMontage() + ";";
    }

    /**
     * sql语句-delete
     *
     * @return String
     */
    private String deleteSql() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DELETE FROM ")
                .append(this.tableMontage());

        if (this.conditions.size() != 0)
            stringBuilder.append(" WHERE ").append(this.conditionMontage());

        return stringBuilder.toString();
    }

    /**
     * sql语句-insert
     *
     * @return String
     */
    private String insertSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append(this.tables.get(i));
            else
                stringBuilder.append(", ").append(this.tables.get(i));
        }
        stringBuilder.append(" VALUES (");

        String s = null;
        for (String insert : this.inserts) {
            try {
                Integer.parseInt(insert);
            } catch (Exception e) {
                insert = "\"" + insert + "\"";
            }

            if (s == null) {
                stringBuilder.append(insert);
                s = insert;
                continue;
            }

            stringBuilder.append(", ").append(insert);
        }

        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    /**
     * 关闭链接
     *
     * @param conn Connection
     * @param stmt PreparedStatement
     * @param rs ResultSet
     * @throws SQLException 异常
     */
    private void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }

    /**
     * 拼接 condition
     *
     * @return String
     */
    private String conditionMontage() {
        StringBuilder stringBuilder = new StringBuilder();

        if (this.conditions.size() == 0)
            return null;

        Condition condition = null;
        int conditionTime = 0;
        for (Condition condition1 : this.conditions) {
            if (condition == null) {
                condition = condition1;
            }

            if (conditionTime != 0)
                stringBuilder.append(" ").append(condition1.getType()).append(" ");

            stringBuilder.append(condition1.getKey()).append(" = ").append("'").append(condition1.getString()).append("'");
            conditionTime = 1;
        }
        return stringBuilder.toString();
    }

    /**
     * 拼接 create
     *
     * @return String
     */
    private String createMontage() {
        StringBuilder stringBuilder = new StringBuilder();

        if (this.create.size() == 0)
            return null;

        String key = null;
        for (Create create1 : this.create) {
            if (key != null)
                stringBuilder.append(", ");

            key = create1.getName();
            stringBuilder.append(create1.getName());

            String type = create1.getType();
            int length = create1.getLength();

            stringBuilder.append(" ").append(type);
            if (length != 0)
                stringBuilder.append("(").append(length).append(")");
        }
        return stringBuilder.toString();
    }

    /**
     * 拼接 set
     *
     * @return String
     */
    private String setMontage() {
        StringBuilder stringBuilder = new StringBuilder();

        if (this.sets.size() == 0)
            return null;

        Set set = null;
        for (Set set1 : this.sets) {
            if (set == null) {
                set = set1;
                stringBuilder.append(set1.getKey()).append(" = '").append(set1.getValue()).append("'");
                continue;
            }

            stringBuilder.append(", ").append(set1.getKey()).append(" = '").append(set1.getValue()).append("'");
        }
        return stringBuilder.toString();
    }

    /**
     * 拼接 col
     *
     * @return String
     */
    public String colMontage() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.cols.size(); i++) {
            if (i == 0)
                stringBuilder.append(this.cols.get(i));
            else
                stringBuilder.append(", ").append(this.cols.get(i));
        }

        return stringBuilder.toString();
    }

    /**
     * 拼接 table
     *
     * @return 新的Factory
     */
    public String tableMontage() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append(this.tables.get(i));
            else
                stringBuilder.append(", ").append(this.tables.get(i));
        }

        return stringBuilder.toString();
    }
}