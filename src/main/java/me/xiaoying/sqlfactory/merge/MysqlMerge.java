package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.entity.Column;
import me.xiaoying.sqlfactory.sentence.Condition;
import me.xiaoying.sqlfactory.sentence.Create;
import me.xiaoying.sqlfactory.sentence.Insert;
import me.xiaoying.sqlfactory.sentence.Update;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlMerge {
    public static String create(Create create) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < create.getTables().length; i++) {
            stringBuilder.append("CREATE TABLE IF NOT EXISTS ");
            stringBuilder.append("`").append(create.getTables()[i].getName()).append("`(");

            // columns
            for (int j = 0; j < create.getColumns().length; j++) {
                Column column = create.getColumns()[j];
                stringBuilder.append(column.getName()).append(" ").append(column.getType());

                if (column.getLength() > 0) {
                    stringBuilder.append("(").append(column.getLength());

                    for (int k = 0; k < column.getParameter().length; k++)
                        stringBuilder.append(", ").append(column.getParameter()[k]);

                    stringBuilder.append(")");
                }

                // constraints
                if (column.isNullable())
                    stringBuilder.append(" NULL");
                else
                    stringBuilder.append(" NOT NULL");

                if (column.isAutoIncrement())
                    stringBuilder.append(" AUTO_INCREMENT");

                if (column.isUnique())
                    stringBuilder.append(" UNIQUE");

                if (column.isPrimaryKey())
                    stringBuilder.append(" PRIMARY KEY");

                if (column.getComment() != null && !column.getComment().isEmpty())
                    stringBuilder.append(" COMMENT \"").append(column.getComment()).append("\"");

                if (j == create.getColumns().length - 1)
                    break;

                stringBuilder.append(", ");
            }

            stringBuilder.append(")");

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

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < insert.getTables().length; i++) {
            for (int j = 0; j < insert.getValues().size(); j++) {
                StringBuilder prefixBuilder = new StringBuilder("INSERT INTO `").append(insert.getTables()[i]).append("`");

                StringBuilder columnBuilder = new StringBuilder("(");
                StringBuilder valuesBuilder = new StringBuilder("(");

                Map<String, Object> stringObjectMap = insert.getValues().get(j);

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

                stringBuilder.append(prefixBuilder.append(columnBuilder).append(" VALUES ").append(valuesBuilder)).append(";");

                if (j == stringObjectMap.size() - 1)
                    break;

                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public static String update(Update update) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < update.getTables().length; i++) {
            stringBuilder.append("UPDATE ").append(update.getTables()[i]).append(" SET ");

            for (int j = 0; j < update.getValues().size(); j++) {
                for (String string : update.getValues().get(j).keySet())
                    stringBuilder.append(Column.formatName(string)).append(" = \"").append(update.getValues().get(j).get(string)).append("\"");

                if (j == update.getValues().size() - 1)
                    break;

                stringBuilder.append(", ");
            }

            if (!update.getConditions().isEmpty())
                stringBuilder.append(" WHERE ");

            // conditions
            for (int j = 0; j < update.getConditions().size(); j++) {
                Condition condition = update.getConditions().get(j);

                if (j != 0)
                    stringBuilder.append(" ").append(condition.getConnectionType()).append(" ");

                stringBuilder.append(condition.merge());
            }

            stringBuilder.append(";");

            if (i == update.getTables().length - 1)
                break;

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}