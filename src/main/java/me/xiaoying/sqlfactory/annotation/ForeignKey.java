package me.xiaoying.sqlfactory.annotation;

/**
 * 外键配置
 */
public @interface ForeignKey {

    /** 约束名称 */
    String name();

    /** 外键来源表 */
    String referenceTable();

    /** 外键来源字段 */
    String referenceColumn();
}