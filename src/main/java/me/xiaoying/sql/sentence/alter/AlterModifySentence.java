package me.xiaoying.sql.sentence.alter;

import me.xiaoying.sql.entity.Column;

import java.util.List;

public class AlterModifySentence implements AlterSentence {
    private List<Column> columns;

    public AlterModifySentence(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.columns.size(); i++) {
            stringBuilder.append("MODIFY COLUMN ");
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

        return stringBuilder.toString();
    }
}