package me.xiaoying.sqlfactory.handler;

public interface TypeHandler {
    /**
     * 转化器名称<br>
     * 注解 @Conversion 是匹配此方法的值判断是否使用此转换器
     * */
    String name();

    /** 将数据库中读取到的数据转换成 Java 中对应存储变量的数据类型 */
    Object source(Object object);

    /** 由 Java 变量转换成 数据库支持类型 或 指定格式的内容 进行存储 */
    Object target(Object object);
}