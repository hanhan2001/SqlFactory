# SqlFactory

> I don't want to learn apache's or MyBatis, so I make this repository.
>
> SqlFactory can use in sql database, for example: mysql, sqlite, access.



## Use

If you want use this tool, you need do these.

1. clone this repository.

2. build this project

3. use maven command `maven install:install-file -DgroupId=me.xiaoying -DartifactId=SqlFactory -Dversion=0.0.2 -Dpackaging=jar -Dfile=SqlFactory-V0.0.2.jar`

4. import SqlFactory in `pom.xml`

   ```xml
   <dependency>
       <groupId>me.xiaoying</groupId>
       <artifactId>SqlFactory</artifactId>
       <version>0.0.2</version>
   </dependency>
   ```

If your build tool is gradle, you can set `mavenLocal()` to use SqlFactory, but I tried this way, that can't work, so you need import SqlFactory to your project directory.



## Sentence

> I just make some sentence for sql, maybe I will make more sentence in the future.

| Name   | Description        |
| ------ | ------------------ |
| Create | Create table.      |
| Insert | Insert new record. |
| Delete | Delete record.     |
| Update | Update record.     |
| Select | Select records.    |



## E&D

> These are some example code, you can learn how to use SqlFactory through these.



### Factory

```java
SqlFactory mysql = new MysqlFactory("hostname", 3306, "database", "username", "password");
SqlFactory sqlite = new SqliteFactory("jdbc:sqlite", File);
```



### Table

---

#### Create

> Create a new table.

```java
// make new sentence of Create and table name is "table".
Create create = new Create("table");
// set table columns.
Stack<Column> columns = new Stack<>();
columns.add(new Column("first", "varchar", 255));
columns.add(new Column("second", "int", 0));
// create SqlFactory.
SqlFactory sqlFactory = new MysqlFactory("hostname", 3306, "database", "username", "password");
// set SqlFactory's sentence and run.
sqlFactory.sentence(create).run();
```



#### Select

> Select records.

```java
/**
 * make new sentence of Select and table name is "table".
 * <br>
 * you can set columns for select sentence, just like this.<br>
 * select.column(column1, column2);
 */
Select select = new Select("table");
SqlFactory sqlFactory = new MysqlFactory("hostname", 3306, "database", "username", "password");
/**
 * set SqlFactory's sentence and run.<br>
 * <br>
 * you can do like this to.<br>
 * sqlFactory.sentence(select).condition(new Condition("condition column", "value", ConditionType.EQUAL)).run();
 */
sqlFactory.sentence(select).run();
```



#### Update

> Update record.

```java
// make new sentence for Update and table name is "table".
Update update = new Update("table");
update.set("column", "value");
update.set("column2", "value2");
SqlFactory sqlFactory = new MysqlFactory("hostname", 3306, "database", "username", "password");
sqlFactory.sentence(update).condition(new Condition("column", "vaule", ConditionType)).run();
```



#### Delete

> Like update sentence, so I won't make sample code. (/▽＼)



#### Insert

> Insert new record.

```java
Insert insert = new Insert("table");
// every time insert is called, a new record will be inserted.
insert.insert("value1", "value2", "value3", "value4", "and more...");
insert.insert("k1", "k2", "k3", "k4", "and more...");
SqlFactory sqlFactory = new MysqlFactory("hostname", 3306, "database", "username", "password");
sqlFactory.sentence(insert).run();
```



### Condition

---

#### ConditionType

| Name          | Description |
| ------------- | ----------- |
| EQUAL         | ==          |
| NOT_EQUAL     | <>          |
| GREATER       | >           |
| GREATER_EQUAL | >=          |
| LESS          | <           |
| LESS_EQUAL    | <=          |
| LIKE          | like        |
| AND           | and         |
| OR            | or          |
| IS_NULL       | null        |
| NOT_NULL      | not null    |
| NOT           | not         |

#### Example

> you can set sentence's condition before sentence is runned.

```java
// make new sentence... I use insert as an example.

SqlFactory sqlFactory = new MysqlFactory("hostname", 3306, "database", "username", "password");
// set sentence for sqlFactory
sqlFactory.sentence(insert);
// make new condition
Condition condition = new Condition("column", "value", ConditionType.EQUAL);
sqlFactory.condition(condition);
```

If you want make more condition for a sentence, you can do this.

```java
Condition condition = new Condition("column", "value", ConditionType.EQUAL);
condition.condition(new Condition("column2", "value2", ConditionType.EQUAL));
```

