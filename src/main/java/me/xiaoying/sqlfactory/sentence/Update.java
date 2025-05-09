package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.AutoCondition;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Getter
@Accessors(chain = true)
public class Update extends Sentence {
    private final String[] tables;

    private final Map<Integer, Map<String, Object>> values = new HashMap<>();
    private final List<Where> wheres = new ArrayList<>();

    public Update(Object object) {
        this(object, null);
    }

    public Update(Object object, Class<? extends SqlFactory> factory) {
        super(factory);

        Table table;
        if ((table = object.getClass().getAnnotation(Table.class)) == null)
            throw new RuntimeException(new IllegalArgumentException("Missing @Table annotation."));

        this.tables = table.name();

        for (Field declaredField : object.getClass().getDeclaredFields()) {
            try {
                if (declaredField.getAnnotation(Column.class) == null)
                    continue;

                declaredField.setAccessible(true);

                if (declaredField.getAnnotation(AutoCondition.class) != null || Modifier.isFinal(declaredField.getModifiers())) {
                    this.where(declaredField.getName(), declaredField.get(object));
                    continue;
                }

                Map<String, Object> map = new HashMap<>();

                Object value = declaredField.get(object);
                if (value instanceof String && ((String) value).contains("\""))
                    value = ((String) value).replace("\"", "");

                map.put(declaredField.getName(), value);
                this.values.put(this.values.size(), map);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Update where(String key, Object value) {
        return this.where(key, value, (Where.WhereType) null);
    }

    public Update where(String key, Object value, Where.WhereType... types) {
        return this.where(new Where(key, value, types));
    }

    public Update where(Where where) {
        this.wheres.add(where);
        return this;
    }

    @Override
    public String merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}