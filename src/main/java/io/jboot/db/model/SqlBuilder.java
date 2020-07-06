/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.dialect
 */
public class SqlBuilder {

    private static final String OR = " OR ";
    private static final String AND = " AND ";

    public static void buildMysqlWhereSql(StringBuilder sqlBuilder, List<Column> columns) {
        buildWhereSql(sqlBuilder, columns, '`');
    }

    public static String forDeleteByColumns(List<Join> joins, String table, List<Column> columns, char separator) {
        StringBuilder sql = new StringBuilder(45);
        sql.append("DELETE FROM ").append(separator).append(table).append(separator);

        buildJoinSql(sql, joins, separator);
        buildWhereSql(sql, columns, separator);

        return sql.toString();
    }


    public static void buildWhereSql(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        buildWhereSql(sqlBuilder, columns, separator, true);
    }


    public static void buildWhereSql(StringBuilder sqlBuilder, List<Column> columns, char separator, boolean withWhereKeyword) {
        if (ArrayUtil.isNullOrEmpty(columns)) {
            return;
        }

        StringBuilder whereSqlBuilder = new StringBuilder();
        buildByColumns(whereSqlBuilder, columns, separator);

        String whereSql = whereSqlBuilder.toString();

        if (StrUtil.isNotBlank(whereSql)) {
            sqlBuilder.append(withWhereKeyword ? " WHERE " : "").append(whereSql);
        }

    }

    private static void buildByColumns(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        for (int i = 0; i < columns.size(); i++) {

            Column before = i > 0 ? columns.get(i - 1) : null;
            Column curent = columns.get(i);

            if (curent instanceof Or) {
                continue;
            }
            // sqlPart
            else if (curent instanceof SqlPart) {
                appendSqlPartLogic(sqlBuilder, before, (SqlPart) curent);
            }
            // group
            else if (curent instanceof Group) {
                appendGroupLogic(sqlBuilder, before, (Group) curent, separator);
            }
            // in logic
            else if (Column.LOGIC_IN.equals(curent.getLogic()) || Column.LOGIC_NOT_IN.equals(curent.getLogic())) {
                appendLinkString(sqlBuilder, before);
                appendInLogic(sqlBuilder, curent, separator);
            }
            // between logic
            else if (Column.LOGIC_BETWEEN.equals(curent.getLogic()) || Column.LOGIC_NOT_BETWEEN.equals(curent.getLogic())) {
                appendLinkString(sqlBuilder, before);
                appendBetweenLogic(sqlBuilder, curent, separator);
            }
            // others
            else {
                appendLinkString(sqlBuilder, before);
                appendColumnName(sqlBuilder, curent, separator);

                if (curent.hasPara()) {
                    sqlBuilder.append('?');
                }
            }
        }
    }


    private static void appendSqlPartLogic(StringBuilder sqlBuilder, Column before, SqlPart sqlPart) {
        if (!sqlPart.isWithoutLink()) {
            appendLinkString(sqlBuilder, before);
        }
        sqlBuilder.append(' ').append(sqlPart.getSql()).append(' ');
    }


    private static void appendColumnName(StringBuilder sqlBuilder, Column column, char separator) {
        if (column.getName().contains(".")) {
            sqlBuilder.append(column.getName())
                    .append(' ')
                    .append(column.getLogic())
                    .append(' ');
        } else {
            sqlBuilder.append(separator)
                    .append(column.getName())
                    .append(separator)
                    .append(' ')
                    .append(column.getLogic())
                    .append(' ');
        }
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
        for (int i = 0; i < values.length; i++) {
            sqlBuilder.append('?');
            if (i != values.length - 1) {
                sqlBuilder.append(',');
            }
        }
        sqlBuilder.append(')');
    }


    public static void appendBetweenLogic(StringBuilder sqlBuilder, Column column, char separator) {
        sqlBuilder.append(separator)
                .append(column.getName())
                .append(separator)
                .append(' ')
                .append(column.getLogic());

        sqlBuilder.append(" ? AND ?");
    }

    public static StringBuilder forFindByColumns(List<Join> joins, String table, String loadColumns, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(loadColumns)
                .append(" FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        buildJoinSql(sqlBuilder, joins, separator);

        buildWhereSql(sqlBuilder, columns, separator);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder;
    }

    public static String forPaginateFrom(List<Join> joins, String table, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder(" FROM ")
                .append(separator)
                .append(table)
                .append(separator);


        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder.toString();
    }

    private static void buildJoinSql(StringBuilder sqlBuilder, List<Join> joins, char separator) {
        if (joins == null || joins.isEmpty()) {
            return;
        }
        for (Join join : joins) {
            if (!join.isEffective()) {
                continue;
            }

            sqlBuilder.append(join.getType())
                    .append(separator)
                    .append(join.getTable())
                    .append(separator);

            if (StrUtil.isNotBlank(join.getAs())) {
                sqlBuilder.append(" AS ").append(join.getAs());
            }

            sqlBuilder.append(" ON ")
                    .append(join.getOn());
        }
    }


    public static String forFindCountByColumns(List<Join> joins, String table, List<Column> columns, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT count(*) FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        buildJoinSql(sqlBuilder, joins, separator);
        buildWhereSql(sqlBuilder, columns, separator);

        return sqlBuilder.toString();
    }
}
