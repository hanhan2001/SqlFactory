package me.xiaoying.mf;

import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        SqlFactory sqlFactory = new SqlFactory("jdbc:mysql://localhost", 3306, "testbot", "root", "123456");
        Map<String, List<Object>> map = sqlFactory.type(SqlType.SELECT)
                .table("data_common_cmd_first")
                .cols("usefor", "cmd", "alias", "description", "cmduse")
                .condition("alias", "help,cmd").run();
    }
}