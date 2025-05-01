package me.xiaoying.sqlfactory.annotation;

import me.xiaoying.sqlfactory.ColumnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    ColumnType type() default ColumnType.AUTO;

    int length();

    String[] parameter() default {};

    String comment() default "";

    String defaultValue() default "";

    boolean nullable() default false;

    boolean primaryKey() default false;

    boolean autoIncrement() default false;

    boolean unique() default false;
}