package me.xiaoying.sqlfactory;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Table;
import me.xiaoying.sqlfactory.factory.MysqlFactory;
import me.xiaoying.sqlfactory.sentence.Condition;
import me.xiaoying.sqlfactory.sentence.Create;
import me.xiaoying.sqlfactory.sentence.Insert;

public class Test {
    public static void main(String[] args) {
        MysqlFactory factory = new MysqlFactory("localhost", 3307, "authorize", "root", "root", 1);
//        SqliteFactory factory = new SqliteFactory(new File("C:/Users/Administrator/Desktop/test.db"), 1);

        System.out.println(new Condition("name", "xiaoying", Condition.ConditionType.NOT, Condition.ConditionType.LIKE));
        System.out.println(new Condition("age", "nihao", Condition.ConditionType.BETWEEN_AND));

//        long start;
//
//        start = System.currentTimeMillis();
//        System.out.println(new Create(TestTable.class));
//        factory.run(new Create(TestTable.class));
//        System.out.println(System.currentTimeMillis() - start);
//
//        start = System.currentTimeMillis();
//        System.out.println(new Create(new NewTable()));
//        factory.run(new Create(NewTable.class));
//        System.out.println(System.currentTimeMillis() - start);
//
//        NewTable newTable = new NewTable()
//                .setUuid(190)
//                .setName("test123")
//                .setEmail("test@asdasdtest.com")
//                .setAge(10)
//                .setPhone("456123789")
//                .setGroup("defaawenbrult");
//        NewTable newTable1 = new NewTable()
//                .setUuid(189)
//                .setName("te12st")
//                .setEmail("test@teffwqst.com")
//                .setAge(10)
//                .setPhone("4561488484823789")
//                .setGroup("defauadfglt");
//        Insert insert = new Insert();
//        insert.insert(newTable, newTable1);
//        System.out.println(insert);
//        factory.run(insert);
    }
}

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = {"test1", "test2"}, comment = "测试表格")
class NewTable {
    @Column(length = 9, autoIncrement = true, type = ColumnType.INT, primaryKey = true)
    private int uuid;

    @Column(length = 255)
    private String name;

    @Column(length = 255, nullable = true)
    private String email;

    @Column(length = 3, comment = "年龄")
    private int age;

    // 此处做手动指定类型演示
    // 可能存在查询错误问题
    @Column(length = 11, type = ColumnType.BIGINT)
    private String phone;

    @Column(length = 255)
    private String group;
}

@Table(name = "test")
class TestTable {
    @Column(length = 255, nullable = true)
    String name;

    @Column(length = 3, unique = true)
    int age;

    @Column(length = 11, type = ColumnType.BIGINT)
    String phone;
}