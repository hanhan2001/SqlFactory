package me.xiaoying.mf;

import org.apache.ibatis.jdbc.SqlRunner;
import java.sql.Connection;

public class SQL extends AbstractSQL<SQL> {

    private SqlRunner sqlRunner;

    public SqlRunner createSqlRunner(Connection connection) {
        this.sqlRunner = new SqlRunner(connection);
        return this.sqlRunner;
    }

    @Override
    public SQL getSelf() {
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /*private static final String AND = ") \nAND (";
    private static final String OR = ") \nOR (";
    public static void main(String[] args) {
        System.out.println(AND);
        org.apache.ibatis.jdbc.SQL sql = new org.apache.ibatis.jdbc.SQL();
    }*/
}
