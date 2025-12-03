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

    /** 传递参数<br>
     * 例如 decimal(10, 2)，其中 2 就取自 parameter 中的 0 下标对应的值
     */
    String[] parameter() default {};

    /** 注释 */
    String comment() default "";

    /** 默认值 */
    String defaultValue() default "";

    /** 是否允许为空 */
    boolean nullable() default true;

    /** 主键 */
    boolean primaryKey() default false;

    /**
     * 外键<br>
     */
    ForeignKey foreignKey() default @ForeignKey(name = "", referenceTable = "", referenceColumn = "");

    /** 是否为自增列 */
    boolean autoIncrement() default false;

    /** 唯一 */
    boolean unique() default false;
}