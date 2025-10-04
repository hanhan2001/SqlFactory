package me.xiaoying.sqlfactory.merge;

import me.xiaoying.sqlfactory.sentence.Create;

import java.util.List;

public class PostgreSqlMerge {
    // 我了解到的 sql 语句差异打的只有 commit 的操作不同，所以单独实现新的 CREATE 处理
    public static List<String> create(Create create) {
        return null;
    }
}