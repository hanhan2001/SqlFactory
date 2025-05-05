import me.xiaoying.sqlfactory.annotation.AutoCondition;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Param;
import me.xiaoying.sqlfactory.annotation.Table;
import me.xiaoying.sqlfactory.config.MysqlConfig;
import me.xiaoying.sqlfactory.factory.MysqlFactory;
import me.xiaoying.sqlfactory.sentence.Create;
import me.xiaoying.sqlfactory.sentence.Insert;
import me.xiaoying.sqlfactory.sentence.Select;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        MysqlFactory factory = new MysqlFactory(new MysqlConfig("root", "root", "localhost", 3307, "authorize"));

//        factory.run(new Create(MyTable.class));
//
//        MyTable t1 = new MyTable("xiaoying", 19);
//        MyTable t2 = new MyTable("xiaotan", 20);
//        MyTable t3 = new MyTable("hanhan", 88);
//        MyTable t4 = new MyTable("suanju", 100);
//
//        factory.run(new Insert().insert(t1, t2, t3, t4));

        Select select = new Select(MyTable.class);
        System.out.println(select);
        List<Object> run = factory.run(select);

        for (Object o : run) {
            System.out.println("Class: " + o.getClass());

            MyTable table = (MyTable) o;

            System.out.println("Name: " + table.getName());
            System.out.println("Age: " + table.getAge());
        }

        System.out.println("--------------");
        for (Object o : factory.run(new Select(new MyTable("a", 19)))) {
            MyTable table = (MyTable) o;
            System.out.println(table.getName());
        }
    }
}

@Table(name = "mytable_1")
class MyTable {
    @Column(length = 255)
    private String name;

    @AutoCondition
    @Column(length = 3)
    private int age;

    public MyTable(@Param("name") String name, @Param("age") int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }
}