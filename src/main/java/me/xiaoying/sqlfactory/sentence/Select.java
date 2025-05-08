package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Select extends Sentence {
    private final String[] tables;

    private final Class<?> clazz;
    private final List<Where> wheres = new ArrayList<>();

    private Constructor<?> constructor;
    private final Map<String, Integer> parameters = new HashMap<>();

    public Select(Object object) {
        this(object, null);
    }

    public Select(Class<?> clazz) {
        this(clazz, null);
    }

    public Select(Object object, Class<? extends SqlFactory> factory) {
        this(object.getClass(), factory);

        for (Field declaredField : object.getClass().getDeclaredFields()) {
            if (!Modifier.isFinal(declaredField.getModifiers()) && declaredField.getAnnotation(AutoCondition.class) == null)
                continue;

            if (declaredField.getAnnotation(Column.class) == null)
                continue;

            declaredField.setAccessible(true);
            try {
                this.wheres.add(new Where(declaredField.getName(), declaredField.get(object)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Select(Class<?> clazz, Class<? extends SqlFactory> factory) {
        super(factory);

        Table table;
        if ((table = clazz.getAnnotation(Table.class)) == null)
            throw new RuntimeException(new IllegalArgumentException("Missing @Table annotation"));

        this.tables = table.name();
        this.clazz = clazz;

        for (Constructor<?> declaredConstructor : this.clazz.getDeclaredConstructors()) {
            if (declaredConstructor.getAnnotation(AutoConstructor.class) != null) {
                declaredConstructor.setAccessible(true);
                this.constructor = declaredConstructor;
                break;
            }

            boolean hasParam = false;

            for (int i = 0; i < declaredConstructor.getParameters().length; i++) {
                Parameter parameter = declaredConstructor.getParameters()[i];

                Param param;
                if ((param = parameter.getAnnotation(Param.class)) == null)
                    continue;

                if (!hasParam)
                    hasParam= true;

                this.parameters.put(param.value(), i);
            }

            if (!hasParam) {
                this.parameters.clear();
                continue;
            }

            declaredConstructor.setAccessible(true);
            this.constructor = declaredConstructor;
        }
    }

    public Select where(String key, Object value) {
        return this.where(key, value, (Where.ConditionType) null);
    }

    public Select where(String key, Object value, Where.ConditionType... types) {
        return this.where(new Where(key, value, types));
    }

    public Select where(Where condition) {
        this.wheres.add(condition);
        return this;
    }

    @Override
    public String merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}