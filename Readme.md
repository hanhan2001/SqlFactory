# SqlFactory



**放弃维护了，我发现更合适的 orm 工具。**

**最初我认为别人做得到的，我也能做到。因此，我决定创建这个项目，并且花费了一定的时间进行维护。**

**不过经过近期日常工作和比赛当中，我意识到我走错了路子。**

**我想说，或许给我足够的时间，我能一个人从零搭建出一个工具链甚至是一个生态。**

**可一次团队赛，我包揽下所有人的任务，仅取得省二的名额，我才意识到我骨子里的傲慢。**

**人类社会不是靠一个人推动的，想为他人考虑的前提是别人是否允许你替他考虑。**

**专业的事交给专业的人做，给别人留活路就是给自己留退路。**

**我需要重新规划我的人生，项目留档——给愿意专研的人借鉴。**



> 在这之前我已经了解过 Mybatis 一类的数据库操作工具，但我觉得将 sql 配置写在 resources 中不太高档并且如果要修改太过麻烦，所以我自己写了 SqlFactory

环境: `Java- 8`

默认依赖版本:

- `Mysql` -> com.mysql:mysql-connector-j -> 9.5.0
- `Sqlite` -> org.xerial:sqlite-jdbc -> 3.49.1.0
- `Postgresql` -> org.postgresql -> 42.7.8



## 功能特性

- 自动拼接 Sql 语句
- 数据库连接池
- 高自定义
- 简单快捷
- 快速启动



## 🤚默认支持

> SqlFactory 已经提供了部分数据库和 sql 语句的处理代码
>
> 部分常用的 sql 语句会在未来进行添加

#### 数据库

- Mysql
- Sqlite
- PostgreSql

#### SQL 语句

- Insert
- Delete
- Update
- Select
- Create



## 🌳项目结构

```
src\main\java\me\xiaoying\sqlfactory/.
├─annotation      #注解，如 @Table, @Column
├─config          #数据库配置类
├─entity          #Table Column 实体
├─factory         #数据库工厂
├─merge           #不同数据库的 sql 语句拼接处理
└─sentence        #sql 语句实体类
```



## ⚙️配置依赖

> 已经上传到 312Hz 仓库，或许在未来会删除

### 项目中引用

#### Maven

```xml
<repository>
	<id>312hz</id>
    <name>312Hz Maven</name>
    <url>https://312hz.github.io/maven-repository</url>
</repository>

<dependency>
    <groupId>me.xiaoying</groupId>
    <artifactId>sqlfactory</artifactId>
    <version>{$version}</version>
</dependency>
```

#### Gradle(kts)

```kotlin
maven("https://312Hz.github.io/maven-repository")

implementation("me.xiaoying:sqlfactory:$version")
```



## 🧭基础示例

### 创建表

假设有表结构如下

```
╭─────────────────╮
│ name      │ age │
├─────────────────┤
│ ZhangMing │ 99  │
│ XiaoYing  │ 20  │
│ XiaoTan   │ 3   │
╰─────────────────╯
```

对照此表则需要在 Java 中指定 class 如下

```java
import me.xiaoying.sqlfactory.annotation.Table;

// class 需要指定使用 @Table 注解，否则无法识别成 table 对象
@Table
public class MyTable {
    // 对变量的修饰符没有限制，只要有 @Column 注解则会被识别成列对象
    @Column(length=255)
    private String name;
    
    // @Column name 可以指定当前变量在表中的名称是什么，在创建表时将使用 @Column 指定的名称设置列名称
    @Column(length=3, name="age")
    private int other;
}
```

执行创建表

```java
public class Main {
    public static void main(String[] args) {
        SqliteFactory factory = new SqliteFactory(new File("C:/Users/Administrator/Desktop/test.db"));
        factory.run(new Create(MyTable.class);
        // 或者使用 factory.run(new Create(new MyTable())) 是一样的
    }
}
```

