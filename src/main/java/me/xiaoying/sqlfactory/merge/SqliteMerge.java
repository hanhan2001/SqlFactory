package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.ColumnType;
import me.xiaoying.sqlfactory.entity.Column;
import me.xiaoying.sqlfactory.entity.Table;
import me.xiaoying.sqlfactory.sentence.Create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteMerge {

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
            columnsBuilder.append(column.getName()).append(" ");

            // 类型过滤
            // SQLite 与 Mysql 数据类型不完全相同
            switch (column.getType()) {
                case INT:
                case BIGINT:
                    columnsBuilder.append("INTEGER");
                    break;
                default:
                    columnsBuilder.append(column.getType());
                    break;
            }

            // 拼接字段长度及参数
            // 需要注意的是在 SQLite, Postgresql 中 Blob 存储参数不同，Mysql 不需要再进行修改，但需要单独适配 SQLite 及 Postgresql
            if (column.getLength() > 0 || column.getParameter().length != 0 && column.getType() != ColumnType.BLOB) {
                columnsBuilder.append("(");

                boolean flag = false;
                if (column.getLength() > 0) {
                    columnsBuilder.append(column.getLength());
                    flag = true;
                }

                if (flag && column.getParameter().length != 0)
                    columnsBuilder.append(", ");

                for (int j = 0; j < column.getParameter().length; j++) {
                    columnsBuilder.append(column.getParameter()[i]);

                    if (i == column.getParameter().length - 1)
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
                columnsBuilder.append(" NOT NULL PRIMARY KEY AUTOINCREMENT ");
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
            primaryBuilder.append("ALTER TABLE %table% ADD pk_primary_%table% PRIMARY KEY (");

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

                StringBuilder foreignBuilder = new StringBuilder("ALTER TABLE %table% ADD FOREIGN KEY (");
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
            if (!foreignBuilders.isEmpty()) {
                list.add("PRAGMA foreign_keys = ON;");

                for (String foreignBuilder : foreignBuilders)
                    list.add(foreignBuilder.replace("%table%", table.getName()));
            }
        }

        return list;
    }
}