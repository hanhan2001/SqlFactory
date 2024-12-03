package me.xiaoying.sql.sentence.alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlterDropColumn implements AlterSentence {
    private final List<String> columns = new ArrayList<>();

    public AlterDropColumn(String column, String... columns) {
        this.columns.add(column);
        if (columns.length != 0)
            this.columns.addAll(Arrays.asList(columns));
    }

    @Override
    public String merge() {
        StringBuilder stringBuilder = new StringBuilder("DROP COLUMN ");
        return "";
    }
}