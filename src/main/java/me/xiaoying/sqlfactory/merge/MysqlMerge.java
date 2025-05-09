package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.entity.Column;
import me.xiaoying.sqlfactory.entity.Table;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlMerge {
    public static List<String> create(Create create) {
        // columns
        StringBuilder columnsBuilder = new StringBuilder();

        for (int i = 0; i < create.getColumns().length; i++) {
            Column column = create.getColumns()[i];
            columnsBuilder.append(column.getName()).append(" ").append(column.getType());

            if (column.getLength() > 0) {
                columnsBuilder.append("(").append(column.getLength());

                for (String string : column.getParameter())
                    columnsBuilder.append(", ").append(string);

                columnsBuilder.append(")");
            }

            // constraints
            if (column.isNullable())
                columnsBuilder.append(" NULL");
            else
                columnsBuilder.append(" NOT NULL");

            if (column.isAutoIncrement())
                columnsBuilder.append(" AUTO_INCREMENT");

            if (column.isUnique())
                columnsBuilder.append(" UNIQUE");

            if (column.isPrimaryKey())
                columnsBuilder.append(" PRIMARY KEY");

            if (column.getComment() != null && !column.getComment().isEmpty())
                columnsBuilder.append(" COMMENT '").append(column.getComment()).append("'");

            if (i == create.getColumns().length - 1)
                break;

            columnsBuilder.append(", ");
        }

        List<String> list = new ArrayList<>();

        for (Table table : create.getTables()) {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `").append(table.getName()).append("`(").append(columnsBuilder).append(")");

            if (create.getTables().length > 0 && create.getTables()[0].getComment() != null && !create.getTables()[0].getComment().isEmpty())
                stringBuilder.append(" COMMENT '").append(create.getTables()[0].getComment()).append("'");

            stringBuilder.append(";");
            list.add(stringBuilder.toString());
        }

        return list;
    }

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