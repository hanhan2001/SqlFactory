package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Table;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Accessors(chain = true)
public class Update extends Sentence {
    private final String[] tables;

    private final Map<Integer, Map<String, Object>> values = new HashMap<>();
    private final List<Condition> conditions = new ArrayList<>();

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
            if (declaredField.getAnnotation(Column.class) == null)
                continue;

            declaredField.setAccessible(true);

            try {
                Map<String, Object> map = new HashMap<>();
                map.put(declaredField.getName(), declaredField.get(object));
                this.values.put(this.values.size(), map);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Update condition(String key, Object value) {
        return this.condition(key, value, (Condition.ConditionType) null);
    }

    public Update condition(String key, Object value, Condition.ConditionType... types) {
        return this.condition(new Condition(key, value, types));
    }

    public Update condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    @Override
    public String merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}