package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.ColumnType;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.Table;
import me.xiaoying.sqlfactory.entity.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Create extends Sentence {
    private final me.xiaoying.sqlfactory.entity.Table[] tables;
    private final Column[] columns;

    public Create(Object object) {
        this(object, null);
    }

    /**
     * 创建 Create 语句
     *
     * @param object Table 对象
     * @param factory Merge 对象，默认使用 SqlFactory，也就是 Mysql 的语句格式
     */
    public Create(Object object, Class<? extends SqlFactory> factory) {
        this(object.getClass(), factory);
    }

    public Create(Class<?> clazz) {
        this(clazz, null);
    }

    public Create(Class<?> clazz, Class<? extends SqlFactory> factory) {
        super(factory);

        Table table = clazz.getAnnotation(Table.class);

        if (table == null)
            throw new RuntimeException(new IllegalArgumentException("Missing @Table annotation."));

        List<Column> columns = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            me.xiaoying.sqlfactory.annotation.Column annotation = declaredField.getAnnotation(me.xiaoying.sqlfactory.annotation.Column.class);

            if (annotation == null)
                continue;

            ColumnType type = annotation.type();
            if (type == ColumnType.AUTO)
                type = ColumnType.getType(declaredField.getType());

            columns.add(new Column(declaredField.getName(), type, annotation.length())
                    .setParameter(annotation.parameter())
                    .setComment(annotation.comment())
                    .setDefaultValue(annotation.defaultValue())
                    .setNullable(annotation.nullable())
                    .setPrimaryKey(annotation.primaryKey())
                    .setForeignKey(annotation.foreignKey())
                    .setAutoIncrement(annotation.autoIncrement())
                    .setUnique(annotation.unique()));
        }

        this.columns = columns.toArray(new Column[0]);

        me.xiaoying.sqlfactory.entity.Table[] tables = new me.xiaoying.sqlfactory.entity.Table[table.name().length];
        for (int i = 0; i < table.name().length; i++)
            tables[i] = new me.xiaoying.sqlfactory.entity.Table(table.name()[i]).setComment(table.comment());

        this.tables = tables;

        this.factory = factory;
    }

    public Create(me.xiaoying.sqlfactory.entity.Table[] tables, Column[] columns) {
        this(tables, columns, null);
    }

    public Create(me.xiaoying.sqlfactory.entity.Table[] tables, Column[] columns, Class<? extends SqlFactory> factory) {
        super(factory);

        this.tables = tables;
        this.columns = columns;
    }

    @Override
    public List<String> merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}