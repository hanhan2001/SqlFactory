# SqlFactory

> 在这之前我已经了解过 Mybatis 一类的数据库操作工具，但我觉得将 sql 配置写在 resources 中不太高档并且如果要修改太过麻烦，所以我自己写了 SqlFactory

环境: `Java- 8`

默认依赖版本:

- `Mysql` -> com.mysql:mysql-connector-j -> 9.2.0
- `Sqlite` -> org.xerial:sqlite-jdbc -> 3.49.1.0
- `Postgresql` -> org.postgresql -> 42.7.5



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
├─annotation #注解，如 @Table, @Column
├─config #数据库配置类
├─entity #Table Column 实体
├─factory #数据库工厂
├─merge #不同数据库的 sql 语句拼接处理
└─sentence #sql 语句实体类
```



## ⚙️配置依赖

> 我没有将 SqlFactory 上传到 Maven 仓库，并且没有搭建个人的仓库，所以需要手动将 SqlFactory 导入本地仓库

### 安装到本地仓库

maven

```
mvn install:install-file -DgroupId=me.xiaoying -DartifactId=sqlfactory -Dversion={下载版本} -Dpackaging=jar -Dfile={下载 jar 路径}
```
gradle

```
./gradlew publishToMavenLocal
```

需要注意的是使用 gradle 需要先编译出 jar 包才会导入到本地仓库。如果你没有安装 SqlFactory 使用的 gradle wrapper 版本(8.9)，则会先下载 gradle(也可以自行修改使用版本)，并且会下载 SqlFactory 使用的所有依赖

### 项目中引用

#### Maven

```xml
<dependency>
    <groupId>me.xiaoying</groupId>
    <artifactId>sqlfactory</artifactId>
    <version>{$version}</version>
</dependency>
```

#### Gradle

```kotlin
implementation("me.xiaoying:sqlfactory:$version")
```



## 📄基础示例
