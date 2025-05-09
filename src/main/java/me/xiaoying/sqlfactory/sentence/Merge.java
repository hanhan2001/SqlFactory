package me.xiaoying.sqlfactory.sentence;

import java.util.List;

public interface Merge<T extends Sentence> {
    List<String> merge(T sentence);
}