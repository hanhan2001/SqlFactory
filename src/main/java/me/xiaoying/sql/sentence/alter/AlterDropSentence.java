package me.xiaoying.sql.sentence.alter;

import me.xiaoying.sql.entity.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlterDropSentence implements AlterSentence {
    private final List<String> columns = new ArrayList<>();

    public AlterDropSentence(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
    }

    public AlterDropSentence(List<Column> columns) {
        columns.forEach(column -> this.columns.add(column.getName()));
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.columns.size(); i++) {
            stringBuilder.append("DROP ").append(this.columns.get(i));

            if (i == this.columns.size() - 1)
                break;

            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }
}