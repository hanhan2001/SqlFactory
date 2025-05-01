package me.xiaoying.sqlfactory.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.xiaoying.sqlfactory.ColumnType;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Column {
    private String name;
    private ColumnType type;
    private int length;
    private String[] parameter = {};
    private String comment = "";
    private String defaultValue = "";
    private boolean nullable;
    private boolean primaryKey;
    private boolean unique;
    private boolean autoIncrement;

    public Column(String name, ColumnType type, int length) {
        this.name = Column.formatName(name);
        this.type = type;
        this.length = length;
    }

    public static String formatName(String columnName) {
        if (!columnName.contains("."))
            columnName = "`" + columnName + "`";
        else {
            StringBuilder stringBuilder = new StringBuilder();
            String[] split = columnName.split("\\.");

            if (split.length > 1)
                stringBuilder.append("`").append(split[0]).append("`.`").append(split[1]).append("`");

            columnName = stringBuilder.toString();
        }

        return columnName;
    }
}