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

import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import io.jboot.db.model.Column;
import io.jboot.exception.JbootException;

import java.util.List;


public class JbootSqlServerDialect extends SqlServerDialect implements IJbootModelDialect {


    @Override
    public String forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {

        StringBuilder sqlBuilder = DialectKit.forFindByColumns(table,loadColumns,columns,orderBy,' ');

        if (limit == null) {
            return sqlBuilder.toString();
        }

        if (limit instanceof Number) {
            StringBuilder ret = new StringBuilder();
            ret.append("SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM ");
            ret.append(" ( SELECT TOP ").append(limit).append(" tempcolumn=0,");
            ret.append(sqlBuilder.toString().replaceFirst("(?i)select", ""));
            ret.append(")vip)mvp ");
            return ret.toString();


        } else if (limit instanceof String && limit.toString().contains(",")) {
            String[] startAndEnd = limit.toString().split(",");
            String start = startAndEnd[0];
            String end = startAndEnd[1];

            StringBuilder ret = new StringBuilder();
            ret.append("SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM ");
            ret.append(" ( SELECT TOP ").append(end).append(" tempcolumn=0,");
            ret.append(sqlBuilder.toString().replaceFirst("(?i)select", ""));
            ret.append(")vip)mvp where temprownumber>").append(start);
            return ret.toString();
        } else {
            throw new JbootException("sql limit is error!,limit must is Number of String like \"0,10\"");
        }

    }


    @Override
    public String forPaginateSelect(String loadColumns) {
        return "SELECT " + loadColumns;
    }


    @Override
    public String forPaginateFrom(String table, List<Column> columns, String orderBy) {
        return DialectKit.forPaginateFrom(table, columns, orderBy, ' ');
    }


}
