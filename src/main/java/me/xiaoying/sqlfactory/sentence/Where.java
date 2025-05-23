package me.xiaoying.sqlfactory.sentence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.xiaoying.sqlfactory.entity.Column;

import java.lang.reflect.Array;
import java.util.*;

public class Where {
    private final Object key;
    private final Object value;
    private final WhereType[] types;

    @Getter
    @Setter
    @Accessors(chain = true)
    private WhereType connectionType = WhereType.AND;

    private final List<Where> conditions = new ArrayList<>();

    public Where(Object key, Object value, WhereType... types) {
        if (key instanceof String)
            this.key = Column.formatName((String) key);
        else
            this.key = key;

        this.value = value;

        this.types = types == null || types.length == 0 || types[0] == null ? new WhereType[] { WhereType.EQUAL } : types;
    }

    public Where condition(String key, Object value, WhereType... types) {
        return this.condition(new Where(key, value, types));
    }

    public Where condition(Where condition) {
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
                case BETWEEN_AND: {
                    if (!this.value.getClass().isArray() && !(this.value instanceof List<?>)) {
                        stringBuilder.append(WhereType.BETWEEN_AND.toString().replace("{}", "\"" + this.value + "\""));
                        break;
                    }

                    List<Object> objects;

                    if (this.value.getClass().isArray()) {
                        objects = new ArrayList<>();
                        for (int j = 0; j < Array.getLength(this.value); j++)
                            objects.add(Array.get(this.value, j));
                    } else
                        objects = (List<Object>) this.value;


                    String string = WhereType.BETWEEN_AND.toString();

                    if (objects.size() == 1)
                        string = string.replace("{}", "\"" + objects.get(0).toString() + "\"");
                    else {
                        string = string.replaceFirst("\\{}", "\"" + objects.get(0) + "\"");
                        string = string.replaceFirst("\\{}", "\"" + objects.get(1) + "\"");
                    }

                    stringBuilder.append(string);
                    break;
                }
                case IN:
                    if (!this.value.getClass().isArray() && !(this.value instanceof List<?>)) {
                        stringBuilder.append("IN (\"").append(this.value).append("\")");
                        break;
                    }

                    stringBuilder.append("IN (");
                    if (this.value.getClass().isArray())
                        for (int j = 0; j < Array.getLength(this.value); j++) {
                            stringBuilder.append("\"").append(Array.get(this.value, j)).append("\"");

                            if (j == Array.getLength(this.value) - 1)
                                break;

                            stringBuilder.append(", ");
                        }
                    else {
                        List<Object> value1 = (List<Object>) this.value;
                        for (int j = 0; j < value1.size(); j++) {
                            stringBuilder.append("\"").append(value1.get(j)).append("\"");

                            if (j == value1.size() - 1)
                                break;

                            stringBuilder.append(", ");
                        }
                    }

                    stringBuilder.append(")");
                    break;
                default:
                    stringBuilder.append(this.types[i]);
                    if (i < this.types.length - 1)
                        break;

                    stringBuilder.append(" \"").append(this.value).append("\"");
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
    public enum WhereType {
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
        NULL("NULL"),
        IS_NULL("IS NULL"),
        IN("IN"),
        BETWEEN_AND("BETWEEN {} AND {}");

        private final String value;

        WhereType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}