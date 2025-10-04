package me.xiaoying.sqlfactory.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换模块
 */
public class TypeHandlerManager {
    private static final Map<String, Callback> source = new HashMap<>();
    private static final Map<String, Callback> target = new HashMap<>();

    public static void registerHandler(String name, Callback source, Callback target) {
        TypeHandlerManager.source.put(name, source);
        TypeHandlerManager.target.put(name, target);
    }

    public static Callback getSource(String name) {
        return TypeHandlerManager.source.get(name);
    }

    public static Callback getTarget(String name) {
        return TypeHandlerManager.target.get(name);
    }

    public interface Callback {
        Object call(Object object);
    }
}