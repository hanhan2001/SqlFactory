package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.AutoCondition;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Conversion;
import me.xiaoying.sqlfactory.annotation.Table;
import me.xiaoying.sqlfactory.handler.TypeHandlerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Delete extends Sentence{
    private final String[] tables;

    private final List<Where> wheres = new ArrayList<>();

    public Delete(Class<?> clazz) {
        this(clazz, null);
    }

    public Delete(Object object) {
        this(object, null);
    }

    public Delete(Object object, Class<? extends SqlFactory> factory) {
        this(object.getClass(), factory);

        for (Field declaredField : object.getClass().getDeclaredFields()) {
            if (!Modifier.isFinal(declaredField.getModifiers()) && declaredField.getAnnotation(AutoCondition.class) == null)
                continue;

            if (declaredField.getAnnotation(Column.class) == null)
                continue;

            declaredField.setAccessible(true);
            try {
                Conversion conversion = declaredField.getAnnotation(Conversion.class);

                if (conversion == null) {
                    this.wheres.add(new Where(declaredField.getName(), declaredField.get(object)));
                    continue;
                }

                TypeHandlerManager.Callback target = TypeHandlerManager.getTarget(conversion.bind());

                if (target == null) {
                    this.wheres.add(new Where(declaredField.getName(), declaredField.get(object)));
                    continue;
                }

                this.wheres.add(new Where(declaredField.getName(), target.call(declaredField.get(object))));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Delete(Class<?> clazz, Class<? extends SqlFactory> factory) {
        super(factory);

        Table table;
        if ((table = clazz.getAnnotation(Table.class)) == null)
            throw new RuntimeException(new IllegalArgumentException("Missing @Table annotation"));

        this.tables = table.name();
    }

    public Delete where(String key, Object value) {
        return this.where(key, value, (Where.WhereType) null);
    }

    public Delete where(String key, Object value, Where.WhereType... types) {
        return this.where(new Where(key, value, types));
    }

    public Delete where(Where where) {
        this.wheres.add(where);
        return this;
    }

    @Override
    public List<String> merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}