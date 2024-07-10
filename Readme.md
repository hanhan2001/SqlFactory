# SqlFactory

> I haven't learnd apache or MyBatis, so I make this project.
>
> SqlFactory can use in sql database, for example: mysql, sqlite, access.



## Use

If you want use SqlFactory, you need download jar in releases or build this repository.

1. Use command of maven: `maven install:install-file -DgroupId=me.xiaoying -DartifactId=SqlFactory -Dversion=0.0.2 -Dpackaging=jar -Dfile=SqlFactory-V0.0.2.jar`
2. Import SqlFactory in `pom.xml`

```xml
<dependency>
	<groupId>me.xiaoying</groupId>
    <artifactId>SqlFactory</artifactId>
    <version>0.0.2</version>
</dependency>
```

If your build tool is Gradle, you can set `mavenLocal()` in `build.gradle`.

Actually, I still can't use SqlFactory, event though I do it, so you need import SqlFactory to your project directory.



## Sentence

> I just make some sentence for sql, maybe I will make more sentence in the future.

| Name   | Description        |
| ------ | ------------------ |
| Create | Create table.      |
| Insert | Insert new record. |
| Delete | Delete record.     |
| Update | Update record.     |
| Select | Select records.    |
| Drop   | Delete table.      |



### Create

> Can't use condition

```java
List<Column> list = new ArrayList();
// new Column("column's name", "column's type", column's size);
list.add(new Column("name", "varchar", 255));
list.add(new Column("age", "int", 3));

Create create = new Create(list, "table's name", "multi tables");

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
sqlFactory.run(create);
```

### Insert

> Can't use condition

```java
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * ╰─────────────────╯
 */

Insert insert = new Insert("table's name", "multi tables");
insert.insert("XiaoYing", 20);
insert.insert("XiaoTan", 3);

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
sqlFactory.run(insert);
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
```



### Delete

```java
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
Delete delete = new Delete("table's name", "multi tables");
delete.condition(new Condition("name", "ZhangMing", Condition.Type.EQUAL));

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
sqlFactory.run(delete);
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
```



### Update

```java
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
Update update = new Update("table's name", "multi tables");
update.set("age", 0);
update.condition(new Condition("name", "ZhangMing", Condition.Type.EQUAL));

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
sqlFactory.run(delete);
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 0   │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
```



### Select

```java
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
List<String> list = new ArrayList();
list.add("name");
list.add("age");
Select select = new Select(list, "table's name", "multi tables");

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
List<Table> tables = sqlFactory.run(select);
/**
 * ╭─────────────────╮
 * │ name      │ age │
 * ├─────────────────┤
 * │ ZhangMing │ 99  │
 * │ XiaoYing  │ 20  │
 * │ XiaoTan   │ 3   │
 * ╰─────────────────╯
 */
```



### Drop

```java
Drop drop = new Drop("table's name", "multi tables");
System.out.println(drop);
// It will print "DROP TABLE `table's name`, `multi tables`" in console

SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");
sqlFactory.run(drop);
```





## Condition

> I don't know how to explaint it, I tried make some code to do example

Condition(Obejct, Object, Condition.Type)



### Type

| Name          | Description       |
| ------------- | ----------------- |
| EQUAL         | ==                |
| NOT_EQUAL     | <>                |
| GREATER       | >                 |
| GREATER_EQUAL | >=                |
| LESS          | <                 |
| LESS_EQUAL    | <=                |
| LIKE          | like              |
| AND           | and               |
| OR            | or                |
| IS_NULL       | null              |
| NOT_NULL      | not null          |
| NOT           | not               |
| IN            | in                |
| BETWEEN_AND   | between {} and {} |



### Normal

```java
// Condition condition = new Condition("field's name", value, Condition.Type);

Condition condition = new Condition("name", "XiaoYing", Condition.Type.EQUAL);
System.out.println(condition);
// It will print "`name` = \"XiaoYing\"" in console
```



### Multi Condition

> This is complicated method, perhaps you can try create some conditions by yourself to comparsion.

```java
Condition condition = new Condition("name", "XiaoYing", Condition.Type.EQUAL);
List<Integer> ageList = new ArrayList();
ageList.add(18);
ageList.add(25);
condition.condition(new Condition("age", ageList, Condition.BETWEEN_AND).setConnectionType(Condition.Type.OR));
System.out.println(condition);
// It will print "(`name` = \"Xiaoying\" OR `age` BETWEEN 18 AND 25)" in console
```

#### Nest

```java
Condition firstCondition = new Condition("name", "XiaoYing", Condition.Type.EQUAL);
firstCondition.condition(new Condition("location", "China", Condition.Type.EQUAL));
System.out.println(firstCondition);
// It will print "(`name` = \"XiaoYing\" AND `location` = \"China\")" in console

List<Integer> ageList = new ArrayList();
ageList.add(18);
ageList.add(25);
Condition secondCondition = new Condition("age", ageList, Condition.Type.BETWEEN_AND);
secondCondition.condition("location", "China", Condition.Type.EQUAL);
// It will print "(`age` BETWEEN 18 AND 25 AND `location` = \"China\")" in console

firstCondition.condition(secondCondition.setConnectionType(ConditionType.OR));
System.out.println(firstCondition);
// It will print "(`name` = \"XiaoYing\" AND `location` = \"China\") OR (`age` BETWEEN 18 AND 25 AND `location` = \"China\")" in console
```



## Factory

> I made Mysql's and Sqlite's Factory.
>
> If you want, you can create new class and extend new class to SqlFactory.class



### Constructor

```java
// Create MysqlFactory
SqlFactory mysqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");

// Create SqliteFactory
SqlFactory sqliteFactory = new SqlFactory(new File("C:/Users/Administrator/Desktop/database.db"));
```



### Run

#### Single Sentence

```java
SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");

List<String> selectColumns = new ArrayList();
Select select = new Select(selectColumns, "table", "multi tables");

List<Table> tables = sqlFactory.run(select);
```



#### Multi Sentence

```java
SqlFactory sqlFactory = new MysqlFactory("localhost", 3306, "database", "usernaem", "password");

Update update = new Update("table", "mulit tables");
update.set("age", 18);
update.set("location", "China");
update.condition(new Condition("name", "XiaoYing"));

List<String> selectColumns = new ArrayList();
Select select = new Select(selectColumns, "table", "multi tables");

// table list just contains select sentence's table
List<Table> tables = sqlFactory.run(update, select);
```
