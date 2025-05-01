package me.xiaoying.sqlfactory;

import lombok.Getter;

import java.util.Date;

@Getter
public enum ColumnType {
    VARCHAR("varchar"),
    INT("int"),
    BIGINT("bigint"),
    FLOAT("float"),
    DOUBLE("double"),
    DATE("date"),
    AUTO("auto");

    private final String value;

    ColumnType(String value) {
        this.value = value;
    }

    public static ColumnType getType(Class<?> clazz) {
        if (clazz == String.class)
            return ColumnType.VARCHAR;
        else if (clazz == Integer.class || clazz == int.class)
            return ColumnType.INT;
        else if (clazz == Long.class || clazz == long.class)
            return ColumnType.BIGINT;
        else if (clazz == Float.class || clazz == float.class)
            return ColumnType.FLOAT;
        else if (clazz == Double.class || clazz == double.class)
            return ColumnType.DOUBLE;
        else if (clazz == Date.class || clazz == java.sql.Timestamp.class)
            return ColumnType.DATE;

        return ColumnType.VARCHAR;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}