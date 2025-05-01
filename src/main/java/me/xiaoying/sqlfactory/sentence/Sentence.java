package me.xiaoying.sqlfactory.sentence;

import me.xiaoying.sqlfactory.SqlFactory;

public abstract class Sentence {
    protected Class<? extends SqlFactory> factory;

    public Sentence(Class<? extends SqlFactory> factory) {
        this.factory = factory;
    }

    public abstract String merge();

    @Override
    public String toString() {
        return this.merge();
    }
}