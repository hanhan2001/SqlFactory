package me.xiaoying.sqlfactory.sentence;

public interface Merge<T extends Sentence> {
    String merge(T sentence);
}