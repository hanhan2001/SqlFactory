package me.xiaoying.sqlfactory.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换模块
 */
public class TypeHandlerManager {
    private static final Map<String, Callback> source = new HashMap<>();
    private static final Map<String, Callback> target = new HashMap<>();

    /**
     * 注册转换器
     *
     * @param typeHandler TypeHandler
     */
    public static void registerHandler(TypeHandler typeHandler) {
        TypeHandlerManager.registerHandler(typeHandler.name(), typeHandler::source, typeHandler::target);
    }

    /**
     * 注册转换器
     *
     * @param name 转换器名称
     * @param source 由数据库内容转换成 Java 变量类型
     * @param target 由 Java 变量类型转换成 数据库可存储 或 指定格式 的内容进行存储
     */
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