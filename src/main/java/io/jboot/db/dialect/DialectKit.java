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
package io.jboot.db.dialect;

import io.jboot.db.model.Column;
import io.jboot.db.model.Group;
import io.jboot.db.model.Or;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.dialect
 */
public class DialectKit {

    public static void buildMysqlWhereSql(StringBuilder sqlBuilder, List<Column> columns) {
        buildWhereSql(sqlBuilder, columns, '`');
    }

    public static String forDeleteByColumns(String table, List<Column> columns, char separator) {
        StringBuilder sql = new StringBuilder(45);
        sql.append("DELETE FROM ").append(separator).append(table).append(separator);
        DialectKit.buildWhereSql(sql, columns, ' ');
        return sql.toString();
    }


    public static void buildWhereSql(StringBuilder sqlBuilder, List<Column> columns, char separator) {

        if (ArrayUtil.isNullOrEmpty(columns)) {
            return;
        }

        sqlBuilder.append(" WHERE ");
        buildByColumns(sqlBuilder,columns,separator);


    }

    private static void buildByColumns(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        for (int i = 0; i < columns.size(); i++) {
            Column curent = columns.get(i);
            Column next = i >= columns.size() - 1 ? null : columns.get(i + 1);
            boolean isLast = i >= columns.size() -1;

            // or
            if (curent instanceof Or) {
                continue;
            }
            // group
            else if (curent instanceof Group) {
                appendGroupLogic(sqlBuilder, ((Group) curent).getColumns().getList(), separator);
            }
            // in logic
            else if (Column.LOGIC_IN.equals(curent.getLogic()) || Column.LOGIC_NOT_IN.equals(curent.getLogic())) {
                appendInLogic(sqlBuilder, curent, separator);
            }

            // between logic
            else if (Column.LOGIC_BETWEEN.equals(curent.getLogic()) || Column.LOGIC_NOT_BETWEEN.equals(curent.getLogic())) {
                appendBetweenLogic(sqlBuilder, curent, separator);
            }
            // others
            else {
                sqlBuilder.append(separator)
                        .append(curent.getName())
                        .append(separator)
                        .append(" ")
                        .append(curent.getLogic());

                if (curent.hasPara()) {
                    sqlBuilder.append(" ?");
                }
            }

            appendLinkString(sqlBuilder, next, isLast);
        }
    }

    private static void appendLinkString(StringBuilder sqlBuilder, Column next, boolean isLast) {
        if (isLast) {
            return;
        }
        sqlBuilder.append(next instanceof Or ? " OR " : " AND ");
    }




    public static void appendGroupLogic(StringBuilder sqlBuilder, List<Column> columns, char separator) {
        if (ArrayUtil.isNullOrEmpty(columns)) {
            return;
        }

        sqlBuilder.append("(");
        buildByColumns(sqlBuilder,columns,separator);
        sqlBuilder.append(")");
    }


    public static void appendInLogic(StringBuilder sqlBuilder, Column column, char separator) {
        sqlBuilder.append(separator)
                .append(column.getName())
                .append(separator)
                .append(" ")
                .append(column.getLogic())
                .append(" ");

        sqlBuilder.append("(");
        Object[] values = (Object[]) column.getValue();
        for (int i = 0; i < values.length; i++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1).append(")");
    }


    public static void appendBetweenLogic(StringBuilder sqlBuilder, Column column, char separator) {
        sqlBuilder.append(separator)
                .append(column.getName())
                .append(separator)
                .append(" ")
                .append(column.getLogic());

        sqlBuilder.append(" ? AND ?");
    }

    public static StringBuilder forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(loadColumns)
                .append(" FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        buildWhereSql(sqlBuilder, columns, separator);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder;
    }

    public static String forPaginateFrom(String table, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder(" FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        buildWhereSql(sqlBuilder, columns, separator);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder.toString();
    }

    public static String forFindCountByColumns(String table, List<Column> columns, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT count(*) FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        buildWhereSql(sqlBuilder, columns, separator);

        return sqlBuilder.toString();
    }
}
