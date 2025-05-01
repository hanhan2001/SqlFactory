package me.xiaoying.sqlfactory.sentence;

import me.xiaoying.sqlfactory.SqlFactory;

public class Update extends Sentence {
    public Update(Object object) {
        this(object, null);
    }

    public Update(Object object, Class<? extends SqlFactory> factory) {
        super(factory);
    }

    @Override
    public String merge() {
        return "";
    }
}