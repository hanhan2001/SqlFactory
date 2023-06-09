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

| Name  | Description                   |
| ----- | ----------------------------- |
| table | set sql statement from tables |

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

run code -> `factory.remoceCondition("k", "v", "OR")`

```yaml
Before: "SELECT * FROM table WHERE `k` = 'dd' AND `k` = 'v' OR `k` = 'v'"
After: "SELECT * FROM table WHERE `k` = 'dd' AND `k` = 'v'"
```

### Desription

| Name            | Description                    |
| --------------- | ------------------------------ |
| condition       | set sql statement conditions   |
| removeCondition | unset sql statement conditions |

### cols

| Name      | Description             | Example                                                      |
| --------- | ----------------------- | ------------------------------------------------------------ |
| condition | sql statement condtions | new MysqlFactory(mysql set).condition(key, value);<br /><br />new MysqlFactory(mysql set).condition("name", "testName"); -> return List |
|           |                         |                                                              |
|           |                         |                                                              |

