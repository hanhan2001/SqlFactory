package me.xiaoying.mf.utils;

/**
 * 工具类 异常
 */
public class ExceptionUtil {
    /**
     * 抛出异常
     *
     * @param exception 异常
     */
    public static void throwException(Exception exception) {
        try {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}