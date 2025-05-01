package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.xiaoying.sqlfactory.entity.Column;

import java.lang.reflect.Array;
import java.util.*;

public class Condition {
    private final Object key;
    private final Object value;
    private final ConditionType[] types;

    @Getter
    @Setter
    @Accessors(chain = true)
    private ConditionType connectionType = ConditionType.AND;

    private final List<Condition> conditions = new ArrayList<>();

    public Condition(Object key, Object value, ConditionType... types) {
        if (key instanceof String)
            this.key = Column.formatName((String) key);
        else
            this.key = key;

        this.value = value;

        this.types = types == null || types.length == 0 || types[0] == null ? new ConditionType[] { ConditionType.EQUAL } : types;
    }

    public Condition condition(String key, Object value, ConditionType... types) {
        return this.condition(new Condition(key, value, types));
    }

    public Condition condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public String merge() {
        if (this.conditions.isEmpty())
            return this.handle();

        StringBuilder stringBuilder = new StringBuilder("(");
        stringBuilder.append(this.handle()).append(" ");

        for (int i = 0; i < this.conditions.size(); i++) {
            stringBuilder.append(this.conditions.get(i).getConnectionType()).append(" ").append(this.conditions.get(i).merge());

            if (i == this.conditions.size() - 1)
                break;

            stringBuilder.append(" ");
        }

        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    public String handle() {
        StringBuilder stringBuilder = new StringBuilder(this.key.toString()).append(" ");

        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i] == null)
                continue;

            switch (this.types[i]) {
                case BETWEEN_AND:
                    if (!this.value.getClass().isArray() && !(this.value instanceof List<?>)) {
                        stringBuilder.append(ConditionType.BETWEEN_AND.toString().replace("{}", "\"" + this.value + "\""));
                        break;
                    }

                    List<Object> objects;

                    if (this.value.getClass().isArray()) {
                        objects = new ArrayList<>();
                        for (int j = 0; j < Array.getLength(this.value); j++)
                            objects.add(Array.get(this.value, j));
                    } else
                        objects = (List<Object>) this.value;


                    String string = ConditionType.BETWEEN_AND.toString();

                    if (objects.size() == 1)
                        string = string.replace("{}", "\"" + objects.get(0).toString() + "\"");
                    else {
                        string = string.replaceFirst("\\{}", "\"" + objects.get(0) + "\"");
                        string = string.replaceFirst("\\{}", "\"" + objects.get(1) + "\"");
                    }

                    stringBuilder.append(string);
                    break;
                default:
                    stringBuilder.append(this.types[i]).append(" \"").append(this.value).append("\"");
                    break;
            }

            if (i == this.types.length - 1)
                break;

            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.merge();
    }

    @Getter
    public enum ConditionType {
        EQUAL("="),
        NOT_EQUAL("<>"),
        GREATER_EQUAL(">="),
        LESS_EQUAL("<="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        LIKE("LIKE"),
        AND("AND"),
        OR("OR"),
        NOT("NOT"),
        NOT_NULL("NOT NULL"),
        IS_NULL("IS NULL"),
        IN("IN"),
        BETWEEN_AND("BETWEEN {} AND {}");

        private final String value;

        ConditionType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}