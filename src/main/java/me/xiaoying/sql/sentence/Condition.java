package me.xiaoying.sql.sentence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sql sentence condition
 */
public class Condition {
    private Object key;
    private final Object value;
    private final Type type;
    private Type connctionType = Type.AND;
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * Constructor<br>
     * If you used {@code Condition.Type.BETWEEN_AND} or {@code Condition.Type.IN}, you need set value's type to List<br>
     * List's size need bigger than 2 when you set connection type of {@code BETWEEN_AND}
     *
     * @param key condition's key
     * @param value condition's value
     * @param type condition's type
     */
    public Condition(Object key, Object value, Type type) {
        if (key instanceof String) {
            if (!key.toString().startsWith("`"))
                key = "`" + key;
            if (!key.toString().endsWith("`"))
                key = key + "`";
        }

        this.key = key;
        if (value instanceof String)
            this.value = "\"" + value + "\"";
        else
            this.value = value;

        this.type = type;
    }

    /**
     * Get connection type between this condition and other conditions
     *
     * @return Condition.Type
     */
    public Type getConnctionType() {
        return this.connctionType;
    }

    /**
     * set the connection type between this condition and other conditions
     *
     * @param type Condition.Type
     * @return Condition
     */
    public Condition setConnectionType(Type type) {
        this.connctionType = type;
        return this;
    }

    /**
     * Add new condition for this
     *
     * @param conditions Condition[]
     * @return Condition
     */
    public Condition condition(Condition... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    /**
     * Merge condition to string
     *
     * @return String
     */
    public String merge() {
        if (this.conditions.isEmpty())
            return this.handleObject();

        StringBuilder stringBuilder = new StringBuilder("(");
        stringBuilder.append(this.handleObject()).append(" ");

        for (int i = 0; i < this.conditions.size(); i++) {
            stringBuilder.append(this.conditions.get(i).getConnctionType().toString()).append(" ").append(this.conditions.get(i).merge());

            if (i == this.conditions.size() - 1)
                break;

            stringBuilder.append(" ");
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private String handleObject() {
        StringBuilder stringBuilder = new StringBuilder();

        // handle multi table
        if (this.key instanceof String && ((String) this.key).contains(".")) {
            this.key = ((String) this.key).replace("`", "");
            String[] split = ((String) this.key).split("\\.");
            this.key = split[0] + ".`" + split[1] + "`";
        }

        // 处理特殊类型
        if (this.type == Type.BETWEEN_AND) {
            String string = this.type.toString();
            if (this.value instanceof List<?> && ((List<?>) this.value).size() >= 2) {
                string = string.replaceFirst("\\{}", ((List<?>) this.value).get(0).toString());
                string = string.replaceFirst("\\{}", ((List<?>) this.value).get(1).toString());
            } else
                string = string.replace("{}", this.value.toString());
            stringBuilder.append(this.key).append(" ");
            stringBuilder.append(string);
            return stringBuilder.toString();
        }

        if (this.type == Type.IN) {
            StringBuilder sb = new StringBuilder(this.key.toString()).append(" ").append(this.type).append(" (");
            if (this.value instanceof List) {
                List<?> list = (List<?>) this.value;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof String)
                        sb.append("\"").append(list.get(i)).append("\"");
                    else
                        sb.append(list.get(i).toString());

                    if (i == ((List<?>) this.value).size() - 1)
                        break;

                    sb.append(", ");
                }
            } else
                sb.append(this.value.toString());
            sb.append(")");
            return stringBuilder.append(sb).toString();
        }

        // merge
        stringBuilder.append(this.key).append(" ");
        stringBuilder.append(this.type).append(" ");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.merge();
    }

    public enum Type {
        EQUAL("="),
        NOT_EQUAL("<>"),
        GREATER_EQUAL(">="),
        LESS("<"),
        LESS_EQUAL("<="),
        LIKE("LIKE"),
        AND("AND"),
        OR("OR"),
        IS_NULL("IS NULL"),
        NOT("NOT"),
        NOT_NULL("NOT NULL"),
        IN("IN"),
        BETWEEN_AND("BETWEEN {} AND {}");

        private String key;

        Type(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }
}