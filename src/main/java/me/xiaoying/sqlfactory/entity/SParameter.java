package me.xiaoying.sqlfactory.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.xiaoying.sqlfactory.annotation.Conversion;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SParameter {
    /** 下标 */
    private final int index;

    /** Conversion 数据 */
    private final Conversion conversion;

    public SParameter(int index, Conversion conversion) {
        this.index = index;
        this.conversion = conversion;
    }

    public boolean hasConv() {
        return this.conversion != null;
    }
}