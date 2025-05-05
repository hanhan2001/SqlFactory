package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.entity.Column;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlMerge {
    public static String create(Create create) {
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
                columnsBuilder.append(" COMMENT \"").append(column.getComment()).append("\"");

            if (i == create.getColumns().length - 1)
                break;

            columnsBuilder.append(", ");
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < create.getTables().length; i++) {
            stringBuilder.append("CREATE TABLE IF NOT EXISTS ");
            stringBuilder.append("`").append(create.getTables()[i].getName()).append("`(").append(columnsBuilder).append(")");

            if (create.getTables().length > 0 && create.getTables()[0].getComment() != null && !create.getTables()[0].getComment().isEmpty())
                stringBuilder.append(" COMMENT \"").append(create.getTables()[0].getComment()).append("\"");

            stringBuilder.append(";");

            if (i == create.getTables().length - 1)
                break;

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public static String insert(Insert insert) {
        if (insert.getValues().isEmpty())
            return "";

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
                valuesBuilder.append("\"").append(value).append("\"");

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

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < insert.getTables().length; i++) {
            for (String s : suffix) {
                if (stringBuilder.length() != 0)
                    stringBuilder.append("\n");

                stringBuilder.append("INSERT INTO `").append(insert.getTables()[i]).append("`").append(s);
            }
        }

        return stringBuilder.toString();
    }

    public static String update(Update update) {
        StringBuilder stringBuilder = new StringBuilder();

        // sets
        StringBuilder setsBuilder = new StringBuilder();
        for (int i = 0; i < update.getValues().size(); i++) {
            for (String string : update.getValues().get(i).keySet())
                setsBuilder.append(Column.formatName(string)).append(" = \"").append(update.getValues().get(i).get(string)).append("\"");

            if (i == update.getValues().size() - 1)
                break;

            setsBuilder.append(", ");
        }

        // conditions
        StringBuilder conditionBuilder = new StringBuilder();
        for (int i = 0; i < update.getConditions().size(); i++) {
            Condition condition = update.getConditions().get(i);

            if (i != 0)
                conditionBuilder.append(" ").append(condition.getConnectionType()).append(" ");

            conditionBuilder.append(condition.merge());
        }

        for (int i = 0; i < update.getTables().length; i++) {
            stringBuilder.append("UPDATE ").append(update.getTables()[i]).append(" SET ");

            stringBuilder.append(setsBuilder);

            if (conditionBuilder.length() != 0)
                stringBuilder.append(" WHERE ").append(conditionBuilder);

            stringBuilder.append(";");

            if (i == update.getTables().length - 1)
                break;

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public static String select(Select select) {
        StringBuilder stringBuilder = new StringBuilder();

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

        // conditions
        StringBuilder conditionBuilder = new StringBuilder();
        for (int i = 0; i < select.getConditions().size(); i++) {
            Condition condition = select.getConditions().get(i);

            if (i != 0)
                conditionBuilder.append(" ").append(condition.getConnectionType()).append(" ");

            conditionBuilder.append(condition.merge());
        }

        // tables
        for (int i = 0; i < select.getTables().length; i++) {
            stringBuilder.append("SELECT ").append(columnBuilder).append(" FROM ").append(select.getTables()[i]);

            if (conditionBuilder.length() != 0)
                stringBuilder.append(" WHERE ").append(conditionBuilder);

            stringBuilder.append(";");

            if (i == select.getTables().length - 1)
                break;

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}