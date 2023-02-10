/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.db.model;

import com.jfinal.kit.LogKit;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class SqlBuilder {

    private static final String OR = " OR ";
    private static final String AND = " AND ";

    public static void buildMysqlWhereSql(StringBuilder sqlBuilder, List<Column> columns) {
        buildWhereSql(sqlBuilder, columns, '`');
    }

    public static String forDeleteByColumns(String alias, List<Join> joins, String table, List<Column> columns, char separator) {
        StringBuilder sqlBuilder = new StringBuilder(45);
        sqlBuilder.append("DELETE FROM ");

        appendTextWithSeparator(sqlBuilder, table, separator);

        buildAlias(sqlBuilder, alias);
        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        return sqlBuilder.toString();
    }


    public static void buildWhereSql(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        buildWhereSql(sqlBuilder, columns, separator, true);
    }


    public static void buildWhereSql(StringBuilder sqlBuilder, List<Column> columns, char separator, boolean appendWhereKeyword) {
        if (ArrayUtil.isNullOrEmpty(columns)) {
            return;
        }

        StringBuilder whereSqlBuilder = new StringBuilder();
        buildByColumns(whereSqlBuilder, columns, separator);

        if (whereSqlBuilder.length() > 0) {
            if (appendWhereKeyword && !isAllGroupByColumns(columns)) {
                sqlBuilder.append(" WHERE ");
            }
            sqlBuilder.append(whereSqlBuilder);
        }
    }

    //fixed: https://gitee.com/JbootProjects/jboot/issues/I3TP7J
    private static boolean isAllGroupByColumns(List<Column> columns) {
        for (Column column : columns) {
            if (!(column instanceof GroupBy)) {
                return false;
            }
        }
        return true;
    }


    private static void buildByColumns(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        for (int i = 0; i < columns.size(); i++) {

            Column before = i > 0 ? columns.get(i - 1) : null;
            Column current = columns.get(i);

            if (current instanceof Or) {
                continue;
            }
            // sqlPart
            else if (current instanceof SqlPart) {
                appendSqlPartLogic(sqlBuilder, before, (SqlPart) current, separator);
            }
            // group
            else if (current instanceof Group) {
                appendGroupLogic(sqlBuilder, before, (Group) current, separator);
            }
            // in logic
            else if (Column.LOGIC_IN.equals(current.getLogic()) || Column.LOGIC_NOT_IN.equals(current.getLogic())) {
                appendLinkString(sqlBuilder, before);
                appendInLogic(sqlBuilder, current, separator);
            }
            // between logic
            else if (Column.LOGIC_BETWEEN.equals(current.getLogic()) || Column.LOGIC_NOT_BETWEEN.equals(current.getLogic())) {
                appendLinkString(sqlBuilder, before);
                appendBetweenLogic(sqlBuilder, current, separator);
            }
            // others
            else {
                appendLinkString(sqlBuilder, before);
                appendColumnName(sqlBuilder, current, separator);

                if (current.hasPara()) {
                    sqlBuilder.append('?');
                }
            }
        }
    }


    private static void appendSqlPartLogic(StringBuilder sqlBuilder, Column before, SqlPart sqlPart, char separator) {
        if (!sqlPart.isWithoutLink()) {
            appendLinkString(sqlBuilder, before);
        }
        sqlPart.build(separator);
        sqlBuilder.append(' ').append(sqlPart.getSql()).append(' ');
    }


    private static void appendColumnName(StringBuilder sqlBuilder, Column column, char separator) {
        appendTextWithSeparator(sqlBuilder, column.getName(), separator);
        sqlBuilder.append(' ')
                .append(column.getLogic())
                .append(' ');
    }


    private static void appendLinkString(StringBuilder sqlBuilder, Column before) {
        if (sqlBuilder.length() == 0 || before == null) {
            return;
        } else {
            sqlBuilder.append(before instanceof Or ? OR : AND);
        }
    }


    public static void appendGroupLogic(StringBuilder sqlBuilder, Column before, Group group, char separator) {
        List<Column> columns = group.getColumns().getList();
        if (ArrayUtil.isNullOrEmpty(columns)) {
            return;
        }

        StringBuilder groupSqlBuilder = new StringBuilder();
        buildByColumns(groupSqlBuilder, columns, separator);

        String groupSql = groupSqlBuilder.toString();
        if (StrUtil.isNotBlank(groupSql)) {
            appendLinkString(sqlBuilder, before);
            sqlBuilder.append('(');
            sqlBuilder.append(groupSql);
            sqlBuilder.append(')');
        }
    }


    public static void appendInLogic(StringBuilder sqlBuilder, Column column, char separator) {

        appendColumnName(sqlBuilder, column, separator);

        sqlBuilder.append('(');

        Object[] values = (Object[]) column.getValue();

        //in 里的参数数量
        int paraCount = 0;
        for (Object v : values) {
            if (v.getClass() == int[].class) {
                paraCount += ((int[]) v).length;
            } else if (v.getClass() == long[].class) {
                paraCount += ((long[]) v).length;
            } else if (v.getClass() == short[].class) {
                paraCount += ((short[]) v).length;
            } else {
                paraCount++;
            }
        }

        for (int i = 0; i < paraCount; i++) {
            sqlBuilder.append('?');
            if (i != paraCount - 1) {
                sqlBuilder.append(',');
            }
        }
        sqlBuilder.append(')');
    }


    public static void appendBetweenLogic(StringBuilder sqlBuilder, Column column, char separator) {
        appendTextWithSeparator(sqlBuilder, column.getName(), separator);
        sqlBuilder.append(' ').append(column.getLogic());
        sqlBuilder.append(" ? AND ?");
    }


    public static void appendTextWithSeparator(StringBuilder sqlBuilder, String text, char separator) {
        if (text.indexOf(".") > 0) {
            sqlBuilder.append(text);
        } else {
            sqlBuilder.append(separator).append(text).append(separator);
        }
    }


    public static StringBuilder forFindByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(loadColumns)
                .append(" FROM ");
        appendTextWithSeparator(sqlBuilder, table, separator);

        buildAlias(sqlBuilder, alias);
        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        orderBy = escapeOrderBySql(orderBy);
        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder;
    }

    //来源于 @link Dialect.java
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
            "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static String replaceOrderBy(String sql) {
        return ORDER_BY_PATTERN.matcher(sql).replaceAll("");
    }

    public static String forPaginateDistinctTotalRow(String select, String sqlExceptSelect, Object ext) {
        if (ext instanceof JbootModel && CPI.hasAnyJoinEffective((JbootModel) ext)) {
            String distinct = JbootModelExts.getDistinctColumn((JbootModel) ext);
            if (StrUtil.isNotBlank(distinct)) {
                return "SELECT count(DISTINCT " + distinct + ") " + replaceOrderBy(sqlExceptSelect);
            }
        }
        return null;
    }


    public static String forPaginateFrom(String alias, List<Join> joins, String table, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder(" FROM ");
        appendTextWithSeparator(sqlBuilder, table, separator);

        buildAlias(sqlBuilder, alias);
        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        orderBy = escapeOrderBySql(orderBy);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder.toString();
    }


    public static void buildJoinSql(StringBuilder sqlBuilder, List<Join> joins, char separator) {
        if (joins == null || joins.isEmpty()) {
            return;
        }
        for (Join join : joins) {
            if (!join.isEffective()) {
                continue;
            }

            sqlBuilder.append(join.getType());
            appendTextWithSeparator(sqlBuilder, join.getTable(), separator);

            buildAlias(sqlBuilder, join.getAs());

            sqlBuilder.append(" ON ")
                    .append(join.getOn());
        }
    }


    public static void buildAlias(StringBuilder sqlBuilder, String alias) {
        if (StrUtil.isNotBlank(alias)) {
            sqlBuilder.append(" AS ").append(alias);
        }
    }


    public static String forFindCountByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT count(" + loadColumns + ") FROM ");
        appendTextWithSeparator(sqlBuilder, table, separator);

        buildAlias(sqlBuilder, alias);
        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        return sqlBuilder.toString();
    }


    public static String escapeOrderBySql(String orignalOrderBy) {
        if (StrUtil.isNotBlank(orignalOrderBy) && !isValidOrderBySql(orignalOrderBy)) {
            LogKit.warn("Sql Warn: order_by value has inject chars and be filtered, order_by value: " + orignalOrderBy);
            return "";
        }
        return orignalOrderBy;
    }


    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    private static String SQL_ORDER_BY_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    private static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_ORDER_BY_PATTERN);
    }
}
