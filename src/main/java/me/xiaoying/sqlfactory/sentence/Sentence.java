package me.xiaoying.sqlfactory.sentence;

import me.xiaoying.sqlfactory.SqlFactory;

import java.util.List;

public abstract class Sentence {
    protected Class<? extends SqlFactory> factory;

    public Sentence(Class<? extends SqlFactory> factory) {
        this.factory = factory;
    }

    public abstract List<String> merge();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : this.merge()) {
            if (stringBuilder.length() != 0)
                stringBuilder.append("\n");

            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }
}