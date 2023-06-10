# MysqlFactory

## SqlType

Use to set sql statement type.

### Example

```java
new MysqlFactory(mysql set).type(SqlType.SELECT);
```

### Enum

| Type   | Description    |
| ------ | -------------- |
| SELECT | select records |
| UPDATE | update records |
| DELETE | delete records |
| INSERT | insert records |

## Methods

### table

that will add sql statement record source table

#### Example

run code -> `factory.table("newtable");`

```Yaml
Before: "SELECT * FROM null"
After: "SELECT * FROM `newtable`"
```

run code -> `factory.table("table1", "table2");`

```yaml
Before: "SELECT * FROM null"
After: "SELECT * FROM `table1`, `table2`"
```

#### Description

| Name  | Description                          |
| ----- | ------------------------------------ |
| table | set sql statement data source tables |

### condition

set sql statements record conditions

#### Bugs

If you set conditions type equal  "OR", and the condition is first, that will useless.

Because Conditions used List to storage, so the sorting method is a-z.

I think it will be fixed in version 0.0.2.

#### Example

run code -> `factory.condition("k", "v")`

```yaml
Before: "SELECT * FROM table"
After: "SELECT * FROM table WHERE `k` = 'v'"
```

run code -> `factory.condition("k", "v").condition("k1", "v1").condition("k2", "v2", "OR");`

```yaml
Before: "SELELT * FROM table"
After: "SELECT * FROM table WHERE `k` = 'v' AND `k1` = 'v1' OR `k2` = 'v2'"
```

run code -> `factory.removeCondition("k")`

```yaml
Before: "SELECT * FROM table WHERE `k` = 'a'"
After: "SELECT * FROM table"
```

run code -> `factory.removeCondition("k", "v")`

```yaml
Before: "SELECT * FROM table WHERE `k` = 'dd' AND `k` = 'v'"
After: "SELECT * FROM table WHERE `k` = 'dd'"
```

run code -> `factory.removeCondition("k", "v", "OR")`

```yaml
Before: "SELECT * FROM table WHERE `k` = 'dd' AND `k` = 'v' OR `k` = 'v'"
After: "SELECT * FROM table WHERE `k` = 'dd' AND `k` = 'v'"
```

#### Desription

| Name            | Description                    |
| --------------- | ------------------------------ |
| condition       | set sql statement conditions   |
| removeCondition | unset sql statement conditions |

### cols

set sql statements get cols

#### Bugs

If not set sql statements source col and use method `factory.run()`. that will return null.

Because `factory.run()` code is:

```java
// head
private List<String> cols = new ArrayList<>();

// run() method some code
ResultSet rs = stmt.executeQuery();
HashMap<String, List<Object>> map = new HashMap<>();
List<Object> list;
while (rs.next()) {
	for (String s : this.cols) {
    	if (map.get(s) == null)
    		list = new ArrayList<>();
    	else
        	list = map.get(s);

        list.add(rs.getObject(s));
        map.put(s, list);
	}
}
// closed connection
return map;
```

while rs loop, cols list is null. So, that will return null.

I think it will be fixed in version 0.0.2.

#### Example

run code -> `factory.cols("c1")`

```yaml
Before: "SELECT * FROM table"
After: "SELECT `c1` FROM table"
```

run code -> `factory.cols("c1", "c2")`

```yaml
Before: "SELECT * FROM table"
After: "SELECT `c1`, `c2` FROM table"
```

run code -> `factory.removeCols("c1")`

```yaml
Before: "SELECT `c1` FROM table"
After: "SELECT * FROM table"
```

#### Description

| Name       | Description                          |
| ---------- | ------------------------------------ |
| cols       | set sql statement data source cols   |
| removeCols | unset sql statement data source cols |

### insert

#### Example

run code -> `factory.insert("ad", "db", "bb")`

```yaml
Before: "INSERT INTO table VALUE()"
After: "INSERT INTO table VALUE('ad', 'db', 'bb')"
```

run code -> `factory.removeInsert("ad")`

```yaml
Before: "INSERT INTO table VALUE('ad', 'db', 'bb')"
After: "INSERT INTO table VALUE('db', 'bb')"
```

#### Description

| Name         | Description                      |
| ------------ | -------------------------------- |
| insert       | set sql statement insert value   |
| removeInsert | unset sql statement insert value |

### set

#### Example

run code -> `factory.set("col1", "value")`

```yaml
Before: "UPDATE table SET"
After: "UPDATE table SET `col1` = 'value'"
```

run code -> factory.removeSet("col1")

```yaml
Before: "UPDATE table SET `col1` = 'value'"
After: "UPDATE table SET"
```

#### Description

| Name      | Description             |
| --------- | ----------------------- |
| set       | set sql statement set   |
| removeSet | unset sql statement set |

## Other Method

### getSql

get factory sql statement, you can get this sql statement and do yourself sql method

#### Example

run code -> `factory.type(SqlType.SELECT).table("table").cols("c1", "c2").condition("c1", "value")`

return -> `SELECT c1, c2 FROM table WHERE c1 = 'value'`

### clear

clear all set
