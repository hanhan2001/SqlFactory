package me.xiaoying.mf;

import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SQLException {
        MysqlFactory mysqlFactory = new MysqlFactory("jdbc:mysql://localhost", 3306, "fly", "root", "root");
//        Map<String, List<Object>> map = mysqlFactory.type(SqlType.SELECT)
//                .table("data_common_cmd_first")
//                .cols("usefor", "cmd", "alias", "description", "cmduse")
//                .condition("alias", "help,cmd").run();

        SQL sqlBuilder = MysqlFactory.createMysql("jdbc:mysql://localhost", 3306, "fly", "root", "root")
                .newSQL();

        SqlRunner sqlRunner = sqlBuilder.createSqlRunner(mysqlFactory.getConnection());
        Map<String, Object> stringObjectMap = sqlRunner.selectOne("select * from slides where slides_id = 1");

        System.out.println(stringObjectMap.toString());

        /**
         * SELECT o.*, s.`title`,s.`content`
         * FROM order_information o
         * INNER JOIN slides s on o.`id_number` != s.`slides_id`
         * WHERE (o.`order_information_id` = s.slides_id)
         * AND (s.title LIKE "轮播图%")
         * ORDER BY o.order_information_id, s.title
         */
        String s = sqlBuilder
                .SELECT("o.*", "s.title", "s.content")
                .FROM("order_information o")
                .INNER_JOIN("slides s on o.`id_number` != s.`slides_id`")
                .WHERE("o.`order_information_id` = s.slides_id")
                .AND()
                .WHERE("s.title LIKE \"轮播图%\"")
                .ORDER_BY("o.order_information_id", "s.title")
                .toString();
        System.out.println(s);
        List<Map<String, Object>> maps = sqlRunner.selectAll(s);
        maps.forEach(mapss -> System.out.println(mapss.toString()));

//        Vertx.factory.vertx();
//        Vertx.vertx().createDnsClient(null);
    }
}