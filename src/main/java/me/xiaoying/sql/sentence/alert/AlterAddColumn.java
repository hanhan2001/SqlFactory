package me.xiaoying.sql.sentence.alert;

import me.xiaoying.sql.entity.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlterAddColumn implements AlterSentence {
    private final List<Column> columns = new ArrayList<>();

    public AlterAddColumn(Column column, Column... columns) {
        this.columns.add(column);
        if (columns.length != 0)
            this.columns.addAll(Arrays.asList(columns));
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("ADD (");
        for (int i = 0; i < this.columns.size(); i++) {
            Column column = this.columns.get(i);

            stringBuilder.append("`").append(column.getName()).append("` ");
            stringBuilder.append(column.getType());
            if (column.getSize() != 0)
                stringBuilder.append("(").append(column.getSize()).append(")");

            if (column.canNull())
                stringBuilder.append(" DEFAULT NULL");
            else
                stringBuilder.append(" NOT NULL");

            if (i == this.columns.size() - 1)
                break;

            stringBuilder.append(", ");
        }
        return stringBuilder.append(")").toString();
    }
}