package me.xiaoying.sql.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Column 列
 */
public class Column {
    private final String name;
    private final String type;
    private long size;
    private String[] parameter = new String[0];

    public Column(String name, String type, String... parameter) {
        this.name = name;
        this.type = type;

        if (parameter.length == 0)
            return;

        if (parameter.length == 1) {
            try {
                this.size = Long.parseLong(parameter[0]);
            } catch (Exception e) {
                this.parameter = parameter;
            }
            return;
        }

        boolean firstIsNumber;
        try {
            this.size = Long.parseLong(parameter[0]);
            firstIsNumber = true;
        } catch (Exception e) {
            firstIsNumber = false;
        }

        if (firstIsNumber) {
            List<String> list = new ArrayList<>(Arrays.asList(parameter).subList(1, parameter.length));
            parameter = list.toArray(new String[0]);
        }

        this.parameter = parameter;
    }

    public Column(String name, String type, long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    /**
     * Only get column's name<br>
     * For example: column
     *
     * @return column's name
     */
    public String getName() {
        String name;
        if (this.name.contains(".")) {
            String[] split = this.name.split("\\.");
            name = split[split.length - 1];
        } else
            name = this.name;
        return name;
    }

    /**
     * Get column full.<br>
     * For example: table.column
     *
     * @return column's full name
     */
    public String getFullName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public long getSize() {
        return this.size;
    }

    public String[] getParameter() {
        return this.parameter;
    }
}