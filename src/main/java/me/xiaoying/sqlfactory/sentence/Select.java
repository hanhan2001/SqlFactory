package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Param;
import me.xiaoying.sqlfactory.annotation.Table;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Select extends Sentence {
    private String[] tables;

    private final Class<?> clazz;
    private final List<Condition> conditions = new ArrayList<>();

    private Constructor<?> constructor = null;

    public Select(Object object) {
        this(object, null);
    }

    public Select(Class<?> clazz) {
        this(clazz, null);
    }

    public Select(Object object, Class<? extends SqlFactory> factory) {
        this(object.getClass(), factory);

        List<String> parameter = new ArrayList<>();

        for (Field declaredField : this.clazz.getDeclaredFields()) {
            if (declaredField.getAnnotation(Column.class) == null)
                continue;

            if (!Modifier.isFinal(declaredField.getModifiers()))
                continue;

            parameter.add(declaredField.getName());

            declaredField.setAccessible(true);
            try {
                this.conditions.add(new Condition(declaredField.getName(), declaredField.get(object)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // constructor
        flag: for (Constructor<?> constructor : this.clazz.getConstructors()) {
            List<String> flagParameter = new ArrayList<>();

            for (int i = 0; i < constructor.getParameters().length; i++) {
                Param param;
                if ((param = constructor.getParameters()[i].getAnnotation(Param.class)) == null)
                    continue;

                if (!parameter.contains(param.value()))
                    continue flag;

                flagParameter.add(param.value());
            }

            if (flagParameter.size() != parameter.size())
                continue;

            if (this.constructor == null) {
                this.constructor = constructor;
                continue;
            }

            if (this.constructor.getParameterCount() > constructor.getParameterCount())
                this.constructor = constructor;
        }
    }

    public Select(Class<?> clazz, Class<? extends SqlFactory> factory) {
        super(factory);

        if (clazz.getAnnotation(Table.class) == null)
            throw new RuntimeException(new IllegalArgumentException("Missing @Table annotation"));

        this.clazz = clazz;
        this.tables = this.clazz.getAnnotation(Table.class).name();
    }

    @Override
    public String merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}