package me.xiaoying.mf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * mybatis AbstractSql copy from
 *
 * https://www.w3cschool.cn/mybatis/k8ay1im3.html 语句构建教程
 * new SQL()
 *     .SELECT("P.ID", "A.USERNAME", "A.PASSWORD", "P.FULL_NAME", "D.DEPARTMENT_NAME", "C.COMPANY_NAME")
 *     .FROM("PERSON P", "ACCOUNT A")
 *     .INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID", "COMPANY C on D.COMPANY_ID = C.ID")
 *     .WHERE("P.ID = A.ID", "P.FULL_NAME like #{name}")
 *     .ORDER_BY("P.ID", "P.FULL_NAME")
 *     .toString();
 * @param <T>
 */
@SuppressWarnings("unused")
public abstract class AbstractSQL<T> {

    private static final String AND = ") \nAND (";
    private static final String OR = ") \nOR (";
    private final SqlStatement sql = new SqlStatement();
    public abstract T getSelf();

    public SqlStatement sql(){
        return sql;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String sql1 = sql().sql(builder);
        return sql1;
    }

    public T UPDATE(String table) {
        sql().statementType = SqlStatement.StatementType.UPDATE;
        sql().tables.add(table);
        return getSelf();
    }

    public T SET(String sets) {
        sql().sets.add(sets);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T SET(String... sets) {
        sql().sets.addAll(Arrays.asList(sets));
        return getSelf();
    }

    public T INSERT_INTO(String tableName) {
        sql().statementType = SqlStatement.StatementType.INSERT;
        sql().tables.add(tableName);
        return getSelf();
    }

    public T VALUES(String columns, String values) {
        sql().columns.add(columns);
        sql().values.add(values);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T INTO_COLUMNS(String... columns) {
        sql().columns.addAll(Arrays.asList(columns));
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T INTO_VALUES(String... values) {
        sql().values.addAll(Arrays.asList(values));
        return getSelf();
    }

    public T SELECT(String columns) {
        sql().statementType = SqlStatement.StatementType.SELECT;
        sql().select.add(columns);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T SELECT(String... columns) {
        sql().statementType = SqlStatement.StatementType.SELECT;
        sql().select.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T SELECT_DISTINCT(String columns) {
        sql().distinct = true;
        SELECT(columns);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T SELECT_DISTINCT(String... columns) {
        sql().distinct = true;
        SELECT(columns);
        return getSelf();
    }

    public T DELETE_FROM(String table) {
        sql().statementType = SqlStatement.StatementType.DELETE;
        sql().tables.add(table);
        return getSelf();
    }

    public T FROM(String table) {
        sql().tables.add(table);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T FROM(String... tables) {
        sql().tables.addAll(Arrays.asList(tables));
        return getSelf();
    }

    public T JOIN(String join) {
        sql().join.add(join);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T JOIN(String... joins) {
        sql().join.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T INNER_JOIN(String join) {
        sql().innerJoin.add(join);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T INNER_JOIN(String... joins) {
        sql().innerJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T LEFT_OUTER_JOIN(String join) {
        sql().leftOuterJoin.add(join);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T LEFT_OUTER_JOIN(String... joins) {
        sql().leftOuterJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T RIGHT_OUTER_JOIN(String join) {
        sql().rightOuterJoin.add(join);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T RIGHT_OUTER_JOIN(String... joins) {
        sql().rightOuterJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T OUTER_JOIN(String join) {
        sql().outerJoin.add(join);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T OUTER_JOIN(String... joins) {
        sql().outerJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T WHERE(String conditions) {
        sql().where.add(conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T WHERE(String... conditions) {
        sql().where.addAll(Arrays.asList(conditions));
        sql().lastList = sql().where;
        return getSelf();
    }

    public T OR() {
        sql().lastList.add(OR);
        return getSelf();
    }

    public T AND() {
        sql().lastList.add(AND);
        return getSelf();
    }

    public T GROUP_BY(String columns) {
        sql().groupBy.add(columns);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T GROUP_BY(String... columns) {
        sql().groupBy.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T HAVING(String conditions) {
        sql().having.add(conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T HAVING(String... conditions) {
        sql().having.addAll(Arrays.asList(conditions));
        sql().lastList = sql().having;
        return getSelf();
    }

    public T ORDER_BY(String columns) {
        sql().orderBy.add(columns);
        return getSelf();
    }

    /**
     * @since 3.4.2
     */
    public T ORDER_BY(String... columns) {
        sql().orderBy.addAll(Arrays.asList(columns));
        return getSelf();
    }



    private static class SqlStatement {
        public SqlStatement() {
        }
        StatementType statementType;
        List<String> sets = new ArrayList<>();
        List<String> select = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        List<String> join = new ArrayList<>();
        List<String> innerJoin = new ArrayList<>();
        List<String> outerJoin = new ArrayList<>();
        List<String> leftOuterJoin = new ArrayList<>();
        List<String> rightOuterJoin = new ArrayList<>();
        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        List<String> orderBy = new ArrayList<>();
        List<String> lastList = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        boolean distinct;

        public enum StatementType {
            DELETE, INSERT, SELECT, UPDATE
        }

        public String sql(StringBuilder stringBuilder) {
            StringBuilder builder = new StringBuilder(stringBuilder);
            if (statementType == null)
                return null;
            String answer;
            switch (statementType) {
                case DELETE:
                    answer = deleteSQL(builder);
                    break;
                case INSERT:
                    answer = insertSQL(builder);
                    break;
                case SELECT:
                    answer = selectSQL(builder);
                    break;
                case UPDATE:
                    answer = updateSQL(builder);
                    break;
                default:
                    answer = null;
            }

            return answer;
        }

        private String selectSQL(StringBuilder builder) {
            if (distinct) {
                sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
            } else {
                sqlClause(builder, "SELECT", select, "", "", ", ");
            }

            sqlClause(builder, "FROM", tables, "", "", ", ");
            joins(builder);
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
            sqlClause(builder, "HAVING", having, "(", ")", " AND ");
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
            return builder.toString();
        }

        private void joins(StringBuilder builder) {
            sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
            sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
            sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
            sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
            sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
        }

        private String insertSQL(StringBuilder builder) {
            sqlClause(builder, "INSERT INTO", tables, "", "", "");
            sqlClause(builder, "", columns, "(", ")", ", ");
            sqlClause(builder, "VALUES", values, "(", ")", ", ");
            return builder.toString();
        }

        private String deleteSQL(StringBuilder builder) {
            sqlClause(builder, "DELETE FROM", tables, "", "", "");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        private String updateSQL(StringBuilder builder) {
            sqlClause(builder, "UPDATE", tables, "", "", "");
            joins(builder);
            sqlClause(builder, "SET", sets, "", "", ", ");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        //conjunction 连词
        private void sqlClause(StringBuilder builder, String keyword, List<String> parts,String open, String close,String conjunction) {
            if (!parts.isEmpty()){
                if (!builder.isEmpty()){
                    builder.append("\n");
                }
                builder.append(keyword);
                builder.append(" ");
                builder.append(open);
                String last = "________";
                for (int i = 0, n = parts.size(); i < n; i++) {
                    String part = parts.get(i);
                    if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
                        builder.append(conjunction);
                    }
                    builder.append(part);
                    last = part;
                }
                builder.append(close);

            }

        }

    }
}
