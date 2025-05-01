package me.xiaoying.sqlfactory.sentence;

import me.xiaoying.sqlfactory.SqlFactory;

import java.util.HashMap;
import java.util.Map;

public class SentenceManager {
    private static final Map<Class<? extends Sentence>, Map<Class<? extends SqlFactory>, Merge<?>>> knownMerge = new HashMap<>();

    public static <T extends Sentence> void registerMerge(Class<T> sentence, Class<? extends SqlFactory> factory, Merge<T> merge) {
        Map<Class<? extends SqlFactory>, Merge<? extends Sentence>> map;

        if ((map = SentenceManager.knownMerge.get(sentence)) == null)
            map = new HashMap<>();

        map.put(factory, merge);

        SentenceManager.knownMerge.put(sentence, map);
    }

    public static <T extends Sentence> Merge<T> getMerge(T sentence) {
        return SentenceManager.getMerge(sentence, SqlFactory.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Sentence> Merge<T> getMerge(T sentence, Class<? extends SqlFactory> factory) {
        if (factory == null)
            factory = SqlFactory.class;

        Map<Class<? extends SqlFactory>, Merge<?>> classMergeMap = SentenceManager.knownMerge.get(sentence.getClass());
        if (classMergeMap == null)
            return null;

        Merge<T> merge;
        if ((merge = (Merge<T>) classMergeMap.get(factory)) == null)
            merge = (Merge<T>) classMergeMap.get(SqlFactory.class);

        return merge;
    }
}