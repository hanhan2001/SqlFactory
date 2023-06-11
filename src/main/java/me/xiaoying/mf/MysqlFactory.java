package me.xiaoying.mf;

import me.xiaoying.mf.entity.Condition;
import me.xiaoying.mf.entity.Create;

import java.sql.*;
import java.util.*;

public class MysqlFactory {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private SqlType type;
    private final Stack<String> cols = new Stack<>();
    private final Stack<String> tables = new Stack<>();
    private final Stack<String> inserts = new Stack<>();
    private final Stack<Condition> conditions = new Stack<>();
    private final Map<String, String> sets = new HashMap<>();
//    private final Map<String, Map<String, Integer>> create = new HashMap<>();
    private final Stack<Create> create = new Stack<>();

    /**
     * 构建 MysqlFactory
     *
     * @param host 主机地址
     * @param port 端口
     * @param database 数据库
     * @param user 用户
     * @param pass 密码
     */
    public MysqlFactory(String host, int port, String database, String user, String pass) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.pass = pass;
    }

    /**
     * 链接 Mysql
     *
     * @return Connection
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.host + ":" + this.port + "/" + this.database, this.user, this.pass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 语句类型
     * 
     * @param type 类型
     */
    public MysqlFactory type(SqlType type) {
        this.type = type;
        return this;
    }

    /**
     * 来源表
     * 
     * @param tables 表
     */
    public MysqlFactory table(String... tables) {
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    /**
     * 移除来源表
     *
     * @param table 表名
     * @return 新的Factory
     */
    public MysqlFactory removeTable(String table) {
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
    public MysqlFactory cols(String... col) {
        this.cols.addAll(Arrays.asList(col));
        return this;
    }

    /**
     * 来源列
     *
     * @param col 列
     * @return 新的Factory
     */
    public MysqlFactory removeCols(String col) {
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

                    list.add(rs.getObject(s));
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
    public MysqlFactory clear() {
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
    public MysqlFactory create(String key, String type, int length) {
        this.create.add(new Create(key, type, length));
        return this;
    }
    /**
     * 创建字段
     *
     * @param key 字段名
     * @return 新的Factory
     */
    public MysqlFactory removeCreate(String key) {
        this.create.remove(key);
        return this;
    }

    /**
     * 判断条件
     *
     * @param key 对象
     * @param value 值
     * @return 新的Factory
     */
    public MysqlFactory condition(String key, String value) {
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
    public MysqlFactory condition(String key, String value, String type) {
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
    public MysqlFactory removeCondition(String key) {
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
    public MysqlFactory removeCondition(String key, String value) {
        this.conditions.removeIf(condition -> condition.getKey().equalsIgnoreCase(value) && condition.getString().equalsIgnoreCase(value));
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
    public MysqlFactory removeCondition(String key, String value, String type) {
        this.conditions.removeIf(condition -> condition.getKey().equalsIgnoreCase(value) && condition.getString().equalsIgnoreCase(value) && condition.getType().equalsIgnoreCase(type));
        return this;
    }

    /**
     * 写入新数据
     *
     * @param key 列名称
     * @return 新的Factory
     */
    public MysqlFactory insert(String... key) {
        this.inserts.addAll(Arrays.asList(key));
        return this;
    }

    /**
     * 移除写入新数据
     *
     * @param key 列名称
     * @return 新的Factory
     */
    public MysqlFactory removeInsert(String key) {
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
    public MysqlFactory set(String key, String value) {
        sets.put(key, value);
        return this;
    }


    /**
     * 移除更新字段
     *
     * @param key 键值
     * @return 新的Factory
     */
    public MysqlFactory removeSet(String key) {
        sets.remove(key);
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
        for (int i = 0; i < this.cols.size(); i++) {
            if (i == 0)
                stringBuilder.append("`").append(this.cols.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.cols.get(i)).append("`");
        }
        stringBuilder.append(" FROM ");
        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append("`").append(this.tables.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.tables.get(i)).append("`");
        }
        stringBuilder.append(this.conditionMontage()).append(";");
        return stringBuilder.toString();
    }

    /**
     * sql语句-create
     * 
     * @return String
     */
    private String createSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE if NOT EXISTS ");

        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append("`").append(this.tables.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.tables.get(i)).append("`");
        }
        stringBuilder.append(" (").append(this.createMontage()).append(");");
        return stringBuilder.toString();
    }

    /**
     * sql语句-update
     *
     * @return String
     */
    private String updateSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ");
        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append("`").append(this.tables.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.tables.get(i)).append("`");
        }

        stringBuilder.append(" SET ");
        String set = null;
        for (String s : this.sets.keySet()) {
            if (set == null) {
                set = s;
                stringBuilder.append("`").append(s).append("` = '").append(this.sets.get(s)).append("'");
                continue;
            }

            stringBuilder.append(", `").append(s).append("` = '").append(this.sets.get(s)).append("'");
        }

        stringBuilder.append(this.conditionMontage()).append(";");
        return stringBuilder.toString();
    }

    /**
     * sql语句-delete
     *
     * @return String
     */
    private String deleteSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ");
        for (int i = 0; i < this.tables.size(); i++) {
            if (i == 0)
                stringBuilder.append("`").append(this.tables.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.tables.get(i)).append("`");
        }

        stringBuilder.append(";");
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
                stringBuilder.append("`").append(this.tables.get(i)).append("`");
            else
                stringBuilder.append(", `").append(this.tables.get(i)).append("`");
        }
        stringBuilder.append(" VALUE (");

        String s = null;
        for (String insert : this.inserts) {
            if (s == null) {
                stringBuilder.append("`").append(insert).append("`");
                s = insert;
                continue;
            }

            stringBuilder.append(", `").append(insert).append("`");
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

        Condition condition = null;
        int conditionTime = 0;
        for (Condition condition1 : this.conditions) {
            if (condition == null) {
                condition = condition1;
                stringBuilder.append(" WHERE ");
            }

            if (conditionTime != 0)
                stringBuilder.append(" ").append(condition1.getType()).append(" ");

            stringBuilder.append("`").append(condition1.getKey()).append("` = ").append("'").append(condition1.getString()).append("'");
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

        String key = null;
        for (Create create1 : this.create) {
            if (key != null)
                stringBuilder.append(", ");

            key = create1.getName();
            stringBuilder.append("`").append(create1.getName()).append("`");

            String type = create1.getType();
            int length = create1.getLength();

            stringBuilder.append(" ").append(type);
            if (length != 0)
                stringBuilder.append("(").append(length).append(")");
        }
        return stringBuilder.toString();
    }
}