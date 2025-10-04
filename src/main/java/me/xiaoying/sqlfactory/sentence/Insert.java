package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Conversion;
import me.xiaoying.sqlfactory.annotation.Table;
import me.xiaoying.sqlfactory.handler.TypeHandlerManager;

import java.lang.reflect.Field;
import java.util.*;

@Getter
public class Insert extends Sentence {
    private String[] tables;
    private final Map<Integer, Map<String, Object>> values = new HashMap<>();

    public Insert() {
        this(null, null);
    }

    /**
     * 通过 Object 内容创建 Insert 语句<br>
     * Object 必须有 Table 注解，否则 IllegalArgumentException
     *
     * @param object 含有 Table annotation 注解或 继承了 Table class 的实体对象
     */
    public Insert(Object object) {
        this(object, null);
    }

    public Insert(Object object, Class<? extends SqlFactory> factory) {
        super(factory);

        this.insert(object);
    }

    public Insert insert(Object object, Object... objects) {
        List<Object> list = new ArrayList<>();
        list.add(object);
        list.addAll(Arrays.asList(objects));

        for (Object o : list) {
            if (o == null)
                continue;

            Table table;
            if ((table = o.getClass().getAnnotation(Table.class)) == null)
                continue;

            this.tables = table.name();

            Map<String, Object> map = new HashMap<>();

            for (Field declaredField : o.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);

                if (declaredField.getAnnotation(Column.class) == null)
                    continue;

                Object value;
                try {
                    value = declaredField.get(o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                Conversion conversion;
                if ((conversion = declaredField.getAnnotation(Conversion.class)) != null) {
                    TypeHandlerManager.Callback target = TypeHandlerManager.getTarget(conversion.bind());

                    if (target != null) value = target.call(value);
                }

                map.put(declaredField.getName(), value);
            }

            this.values.put(this.values.size(), map);
        }
        return this;
    }

    @Override
    public List<String> merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}