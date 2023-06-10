package me.xiaoying.mf;

import java.sql.*;
import java.util.*;

public class MysqlFactory {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private SqlType type;
    private List<String> cols = new ArrayList<>();
    private List<String> tables = new ArrayList<>();
    private List<String> inserts = new ArrayList<>();
    private final Map<String, List<Conditions>> conditions = new HashMap<>();
    private final Map<String, String> sets = new HashMap<>();

    private SQL sql;

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

    public static MysqlFactory createMysql(String host, int port, String database, String user, String pass){
       return new MysqlFactory(host, port, database, user, pass);
    }

    public SQL newSQL(){
       return new SQL();
    }

    /**
     * 链接 Mysql
     *
     * @return Connection
     * @throws SQLException 异常
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
        this.tables = new ArrayList<>(Arrays.asList(tables));
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
        this.cols = new ArrayList<>(Arrays.asList(col));
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
        if (type == null) {
            try {
                throw new Exception("Unset sql type, need to code factory.type(SqlType)");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

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
        List<Conditions> values;
        if (this.conditions.get(key) == null)
            values = new ArrayList<>();
        else
            values = conditions.get(key);

        values.add(new Conditions(value, "AND"));
        this.conditions.put(key, values);
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
        List<Conditions> values;
        if (this.conditions.get(key) == null)
            values = new ArrayList<>();
        else
            values = conditions.get(key);

        if (!type.equalsIgnoreCase("OR") && !type.equalsIgnoreCase("AND")) {
            try {
                throw new Exception("Unknown type, you can set 'OR' or 'AND'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        values.add(new Conditions(value, type));
        this.conditions.put(key, values);
        return this;
    }

    /**
     * 移除判断条件
     * 
     * @param key 对象
     * @return 新的Factory
     */
    public MysqlFactory removeCondition(String key) {
        if (this.conditions.get(key) == null) 
            return this;

        this.conditions.remove(key);
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
        if (this.conditions.get(key) == null)
            return this;

        Conditions conditions = null;
        for (Conditions conditions1 : this.conditions.get(key)) {
            if (!conditions1.getString().equalsIgnoreCase(value))
                continue;

            conditions = conditions1;
        }

        if (conditions == null)
            return this;

        this.conditions.get(key).remove(conditions);
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
        if (this.conditions.get(key) == null)
            return this;

        Conditions conditions = null;
        for (Conditions conditions1 : this.conditions.get(key)) {
            if (!conditions1.getString().equalsIgnoreCase(value))
                continue;

            if (!conditions1.getType().equalsIgnoreCase(type))
                continue;

            conditions = conditions1;
        }

        if (conditions == null)
            return this;

        this.conditions.get(key).remove(conditions);
        return this;
    }

    /**
     * 写入新数据
     *
     * @param key 列名称
     * @return 新的Factory
     */
    public MysqlFactory insert(String... key) {
        this.inserts = new ArrayList<>(Arrays.asList(key));
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
        if (this.type == SqlType.SELECT) {
            return this.selectSql();
        } else if (this.type == SqlType.UPDATE) {
            return this.updateSql();
        } else if(this.type == SqlType.DELETE) {
            return this.deleteSql();
        } else if (this.type == SqlType.INSERT) {
            return this.insertSql();
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
        String condition = null;
        int conditionTime = 0;
        for (String s : this.conditions.keySet()) {
            List<Conditions> list = this.conditions.get(s);
            if (condition == null) {
                condition = s;
                stringBuilder.append(" WHERE ");
            }

            for (int i = 0; i < list.size(); i++) {
                if (i != 0 || conditionTime != 0)
                    stringBuilder.append(" ").append(list.get(i).getType()).append(" ");

                stringBuilder.append("`").append(s).append("` = ").append("'").append(list.get(i).getString()).append("'");
                conditionTime = 1;
            }
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    /**
     * sql语句-create
     * 
     * @return String
     */
    private String createSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE");



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

        String condition = null;
        int conditionTime = 0;
        for (String s : this.conditions.keySet()) {
            List<Conditions> list = this.conditions.get(s);
            if (condition == null) {
                condition = s;
                stringBuilder.append(" WHERE ");
            }

            for (int i = 0; i < list.size(); i++) {
                if (i != 0 || conditionTime != 0)
                    stringBuilder.append(" ").append(list.get(i).getType()).append(" ");

                stringBuilder.append("`").append(s).append("` = ").append("'").append(list.get(i).getString()).append("'");
                conditionTime = 1;
            }
        }
        stringBuilder.append(";");
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

        String condition = null;
        int conditionTime = 0;
        for (String s : this.conditions.keySet()) {
            List<Conditions> list = this.conditions.get(s);
            if (condition == null) {
                condition = s;
                stringBuilder.append(" WHERE ");
            }

            for (int i = 0; i < list.size(); i++) {
                if (i != 0 || conditionTime != 0)
                    stringBuilder.append(" ").append(list.get(i).getType()).append(" ");

                stringBuilder.append("`").append(s).append("` = ").append("'").append(list.get(i).getString()).append("'");
                conditionTime = 1;
            }
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
}

class Conditions {
    String string;
    String type;

    public Conditions(String string, String type) {
        this.string = string;
        this.type = type;
    }

    public String getString() {
        return this.string;
    }

    public String getType() {
        return this.type;
    }
}