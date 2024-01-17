package me.xiaoying.sql;

import me.xiaoying.sql.entity.Column;
import me.xiaoying.sql.entity.Condition;
import me.xiaoying.sql.entity.ConditionType;
import me.xiaoying.sql.entity.Record;
import me.xiaoying.sql.sentence.Insert;
import me.xiaoying.sql.sentence.Select;
import me.xiaoying.sql.sentence.Update;

public class Test {
    public static void main(String[] args) {
        SqlFactory sqlFactory = new MysqlFactory("jdbc:mysql://localhost", 3306, "authorize", "root", "root");
//        Insert insert = new Insert("code_liveget");
//        insert.insert("asd", "fff", "gggg", "asdf", "ggg").insert("fff", "ggg", "hjhj", "gjvdc", "hhbn");
//        sqlFactory.sentence(insert);
        Select select = new Select("code_liveget");
//        select.setColumns(sqlFactory.getColumns("code_liveget"));
        sqlFactory.sentence(select)
                .condition(new Condition("uuid", "2023/12/29-19:40:30", ConditionType.EQUAL));

        System.out.println(sqlFactory);

//        System.out.println(sqlFactory);
//        for (Record record : sqlFactory.run()) {
//            for (Column column : record.getColumns()) {
//                System.out.println(column.getName() + ": " + record.get(column));
//            }
//        }


//        SqlFactory sqlFactory = new MysqlFactory("jdbc:mysql://localhost", 3306, "authorize", "root", "root");
//
//        Update update = new Update("code_liveget");
//        update.set("token", "456c").set("machine", "gghh");
//        sqlFactory.sentence(update).condition(new Condition("uuid", "000000001", ConditionType.EQUAL));
//        System.out.println(sqlFactory);
//        System.out.println(sqlFactory.run());
    }
}