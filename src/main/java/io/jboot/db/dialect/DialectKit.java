/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

    public static void appIfNotEmpty(List<Column> columns, StringBuilder sqlBuilder, char separator) {
        if (ArrayUtil.isNotEmpty(columns)) {
            sqlBuilder.append(" WHERE ");
            int index = 0;
            int last = columns.size() - 1;
            for (Column column : columns) {

                // or
                if (column instanceof Or) {
                    appendOrLogic(sqlBuilder);
                }
                // in logic
                else if (Column.LOGIC_IN.equals(column.getLogic())) {
                    appendInLogic(sqlBuilder, index, last, column, separator);
                }

                // between logic
                else if (Column.LOGIC_BETWEEN.equals(column.getLogic())) {
                    appendBetweenLogic(sqlBuilder, index, last, column, separator);
                }
                // others
                else {
                    sqlBuilder.append(separator)
                            .append(column.getName())
                            .append(separator)
                            .append(column.getLogic());

                    if (column.isMustNeedValue()) {
                        sqlBuilder.append(" ? ");
                    }

                    if (index != last) {
                        sqlBuilder.append(" AND ");
                    }
                }
                index++;
            }
        }
    }

    public static void appendOrLogic(StringBuilder sqlBuilder) {
        // delete last " AND " str
        sqlBuilder.delete(sqlBuilder.length() - 5, sqlBuilder.length())
                .append(" OR ");
    }

    public static void appendInLogic(StringBuilder sqlBuilder, int index, int last, Column column, char separator) {
        sqlBuilder.append(separator)
                .append(column.getName())
                .append(separator)
                .append(column.getLogic());

        sqlBuilder.append("(");
        Object[] values = (Object[]) column.getValue();
        for (int i = 0; i < values.length; i++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1).append(")");
        if (index != last) {
            sqlBuilder.append(" AND ");
        }
    }


    public static void appendBetweenLogic(StringBuilder sqlBuilder, int index, int last, Column column, char separator) {
        sqlBuilder.append(separator)
                .append(column.getName())
                .append(separator)
                .append(column.getLogic());

        sqlBuilder.append(" ? ").append(" AND ").append(" ? ");
        if (index != last) {
            sqlBuilder.append(" AND ");
        }
    }

    public static StringBuilder forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, char separator) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(loadColumns)
                .append(" FROM ")
                .append(separator)
                .append(table)
                .append(separator);

        appIfNotEmpty(columns, sqlBuilder, separator);

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

        appIfNotEmpty(columns, sqlBuilder, separator);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder.toString();
    }
}
