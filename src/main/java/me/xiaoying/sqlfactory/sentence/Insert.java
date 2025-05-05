package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import me.xiaoying.sqlfactory.SqlFactory;
import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.annotation.Table;

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

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null)
                continue;

            Table table;
            if ((table = list.get(i).getClass().getAnnotation(Table.class)) == null)
                continue;

            this.tables = table.name();

            Map<String, Object> map = new HashMap<>();

            for (Field declaredField : list.get(i).getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);

                if (declaredField.getAnnotation(Column.class) == null)
                    continue;

                try {
                    map.put(declaredField.getName(), declaredField.get(list.get(i)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            this.values.put(this.values.size(), map);
        }
        return this;
    }

    @Override
    public String merge() {
        return SentenceManager.getMerge(this, this.factory).merge(this);
    }
}