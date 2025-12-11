package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.entity.Column;
import me.xiaoying.sqlfactory.entity.Table;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlMerge {

    public static List<String> create(Create create) {
        // 主外键
        // 是否有自增值，如存在自增值则不需要处理组合主键
        boolean hasAuto = false;
        List<Column> primaryKey = new ArrayList<>();
        // 一个表有多个外键
        Map<String, List<Column>> foreignKey = new HashMap<>();

        // columns
        StringBuilder columnsBuilder = new StringBuilder();

        for (int i = 0; i < create.getColumns().length; i++) {
            Column column = create.getColumns()[i];
            columnsBuilder.append(column.getName()).append(" ").append(column.getType());

            // 拼接字段长度及参数
            // 需要注意的是在 SQLite, Postgresql 中 Blob 存储参数不同，Mysql 不需要再进行修改，但需要单独适配 SQLite 及 Postgresql
            if (column.getLength() > 0 || column.getParameter().length != 0) {
                columnsBuilder.append("(");

                boolean flag = false;
                if (column.getLength() > 0) {
                    columnsBuilder.append(column.getLength());
                    flag = true;
                }

                if (flag && column.getParameter().length != 0)
                    columnsBuilder.append(", ");

                for (int j = 0; j < column.getParameter().length; j++) {
                    columnsBuilder.append(column.getParameter()[j]);

                    if (j == column.getParameter().length - 1)
                        break;

                    columnsBuilder.append(", ");
                }

                columnsBuilder.append(")");
            }

            if (column.isPrimaryKey() && !hasAuto)
                primaryKey.add(column);

            // 简单处理
            // 不再进行维护
            if (!column.getForeignKey().name().isEmpty() && !column.getForeignKey().referenceColumn().isEmpty() && !column.getForeignKey().referenceTable().isEmpty()) {
                List<Column> columns;

                if ((columns = foreignKey.get(column.getForeignKey().name())) == null)
                    columns = new ArrayList<>();

                columns.add(column);

                foreignKey.put(column.getForeignKey().name(), columns);
            }

            if (column.isNullable())
                columnsBuilder.append(" NULL");
            else if (!column.isAutoIncrement())
                columnsBuilder.append(" NOT NULL");

            if (column.isAutoIncrement()) {
                columnsBuilder.append(" NOT NULL PRIMARY KEY AUTO_INCREMENT");
                // 标记存在子增值
                hasAuto = true;
                primaryKey.clear();
            }

            if (column.isUnique())
                columnsBuilder.append(" UNIQUE");

            if (column.getComment() != null && !column.getComment().isEmpty())
                columnsBuilder.append(" COMMENT '").append(column.getComment()).append("'");

            if (i == create.getColumns().length - 1)
                break;

            columnsBuilder.append(", ");
        }

        /* 约束语句 */
        // 主键
        StringBuilder primaryBuilder = new StringBuilder();
        if (!primaryKey.isEmpty()) {
            primaryBuilder.append("ALTER TABLE %table% ADD CONSTRAINT pk_primary_%table% PRIMARY KEY (");

            for (int i = 0; i < primaryKey.size(); i++) {
                primaryBuilder.append(primaryKey.get(i).getName());

                if (i == primaryKey.size() - 1) {
                    primaryBuilder.append(");");
                    break;
                }

                primaryBuilder.append(", ");
            }
        }

        List<String> foreignBuilders = new ArrayList<>();
        if (!foreignKey.isEmpty()) {
            foreignKey.forEach((name, list) -> {
                if (list.isEmpty())
                    return;

                StringBuilder foreignBuilder = new StringBuilder("ALTER TABLE %table% ADD CONSTRAINT check_primary_").append(name).append(" FOREIGN KEY %table%(");
                // 编写源表列的同时设置对应的参照列
                StringBuilder referenceBuilder = new StringBuilder();

                for (int i = 0; i < list.size(); i++) {
                    foreignBuilder.append(list.get(i).getName());
                    referenceBuilder.append(Column.formatName(list.get(i).getForeignKey().referenceColumn()));

                    if (i == list.size() - 1) {
                        foreignBuilder.append(")");
                        break;
                    }

                    foreignBuilder.append(", ");
                    referenceBuilder.append(", ");
                }

                foreignBuilder.append(" REFERENCES ")
                        .append(list.get(0).getForeignKey().referenceTable())
                        .append("(").append(referenceBuilder).append(")")
                        .append(" ON DELETE CASCADE ON UPDATE CASCADE;");

                foreignBuilders.add(foreignBuilder.toString());
            });
        }

        List<String> list = new ArrayList<>();

        for (Table table : create.getTables()) {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `").append(table.getName()).append("`(").append(columnsBuilder).append(")");

            if (create.getTables().length > 0 && create.getTables()[0].getComment() != null && !create.getTables()[0].getComment().isEmpty())
                stringBuilder.append(" COMMENT '").append(create.getTables()[0].getComment()).append("'");

            stringBuilder.append(";");
            list.add(stringBuilder.toString());

            /* 添加主外键 */
            // 主键
            if (primaryBuilder.length() != 0)
                list.add(primaryBuilder.toString().replace("%table%", table.getName()));
            if (!foreignBuilders.isEmpty())
                for (String foreignBuilder : foreignBuilders)
                    list.add(foreignBuilder.replace("%table%", table.getName()));
        }

        return list;
    }

    /**
     * 应该修改此处代码， Insert 语句可以支持 insert into table(column1, column2.....) VALUES(.....)
     */
    public static List<String> insert(Insert insert) {
        if (insert.getValues().isEmpty())
            return Collections.emptyList();

        // columns and values
        List<String> suffix = new ArrayList<>();

        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        for (int i = 0; i < insert.getValues().size(); i++) {
            Map<String, Object> stringObjectMap = insert.getValues().get(i);

            columnBuilder.append("(");
            valuesBuilder.append("(");

            AtomicInteger index = new AtomicInteger(0);
            stringObjectMap.forEach((column, value) -> {
                columnBuilder.append(Column.formatName(column));
                valuesBuilder.append("'").append(value).append("'");

                if (index.get() == stringObjectMap.size() - 1)
                    return;

                columnBuilder.append(", ");
                valuesBuilder.append(", ");

                index.incrementAndGet();
            });

            columnBuilder.append(")");
            valuesBuilder.append(")");

            suffix.add(columnBuilder + " VALUES " + valuesBuilder + ";");

            columnBuilder.delete(0, columnBuilder.length());
            valuesBuilder.delete(0, valuesBuilder.length());
        }

        List<String> list = new ArrayList<>();
        for (String table : insert.getTables())
            for (String s : suffix)
                list.add("INSERT INTO `" + table + "`" + s);

        return list;
    }

    public static List<String> update(Update update) {
        // sets
        StringBuilder setsBuilder = new StringBuilder();
        for (int i = 0; i < update.getValues().size(); i++) {
            for (String string : update.getValues().get(i).keySet())
                setsBuilder.append(Column.formatName(string)).append(" = '").append(update.getValues().get(i).get(string)).append("'");

            if (i == update.getValues().size() - 1)
                break;

            setsBuilder.append(", ");
        }

        // wheres
        StringBuilder whereBuilder = new StringBuilder();
        for (int i = 0; i < update.getWheres().size(); i++) {
            Where where = update.getWheres().get(i);

            if (i != 0)
                whereBuilder.append(" ").append(where.getConnectionType()).append(" ");

            whereBuilder.append(where.merge());
        }

        List<String> list = new ArrayList<>();
        for (String table : update.getTables()) {
            StringBuilder stringBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");

            stringBuilder.append(setsBuilder);

            if (whereBuilder.length() != 0)
                stringBuilder.append(" WHERE ").append(whereBuilder);

            stringBuilder.append(";");
            list.add(stringBuilder.toString());
        }

        return list;
    }

    public static List<String> select(Select select) {
        // columns
        StringBuilder columnBuilder = new StringBuilder();
        Field[] declaredFields = select.getClazz().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(me.xiaoying.sqlfactory.annotation.Column.class) == null)
                continue;

            String name = Column.formatName(declaredField.getName());

            if (columnBuilder.length() != 0)
                columnBuilder.append(", ");

            columnBuilder.append(name);
        }

        // wheres
        StringBuilder whereBuilder = new StringBuilder();
        for (int i = 0; i < select.getWheres().size(); i++) {
            Where where = select.getWheres().get(i);

            if (i != 0)
                whereBuilder.append(" ").append(where.getConnectionType()).append(" ");

            whereBuilder.append(where.merge());
        }

        List<String> list = new ArrayList<>();
        // tables
        for (String table : select.getTables()) {
            StringBuilder stringBuilder = new StringBuilder("SELECT ").append(columnBuilder).append(" FROM ").append(table);

            if (whereBuilder.length() != 0)
                stringBuilder.append(" WHERE ").append(whereBuilder);

            stringBuilder.append(";");
            list.add(stringBuilder.toString());
        }

        return list;
    }

    public static List<String> delete(Delete delete) {
        // wheres
        StringBuilder whereBuilder = new StringBuilder();
        for (int i = 0; i < delete.getWheres().size(); i++) {
            Where where = delete.getWheres().get(i);

            if (i != 0)
                whereBuilder.append(" ").append(where.getConnectionType()).append(" ");

            whereBuilder.append(where.merge());
        }

        List<String> list = new ArrayList<>();
        // tables
        for (int i = 0; i < delete.getTables().length; i++) {
            StringBuilder stringBuilder = new StringBuilder("DELETE FROM ").append(delete.getTables()[i]);

            if (!delete.getWheres().isEmpty())
                stringBuilder.append(" WHERE ").append(whereBuilder);

            stringBuilder.append(";");
            list.add(stringBuilder.toString());
        }

        return list;
    }
}