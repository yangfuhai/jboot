/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import io.jboot.db.model.Column;
import io.jboot.db.model.Join;
import io.jboot.db.model.SqlBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class JbootClickHouseDialect extends AnsiSqlDialect implements JbootDialect {

    @Override
    public void getModelGeneratedKey(Model<?> model, PreparedStatement pst, Table table) throws SQLException {
        // doNothing() ; clickhouse 不支持生成主键
    }


    @Override
    public String forDbDeleteById(String tableName, String[] pKeys) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);
        StringBuilder sql = new StringBuilder("ALTER TABLE ").append(tableName).append(" DELETE WHERE ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }


    @Override
    public String forModelDeleteById(Table table) {
        String[] pKeys = table.getPrimaryKey();
        StringBuilder sql = new StringBuilder(45);
        sql.append("ALTER TABLE ");
        sql.append(table.getName());
        sql.append(" DELETE WHERE ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }


    @Override
    public void forDbUpdate(String tableName, String[] pKeys, Object[] ids, Record record, StringBuilder sql, List<Object> paras) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        sql.append("ALTER TABLE ").append(tableName).append(" UPDATE ");
        for (Map.Entry<String, Object> e : record.getColumns().entrySet()) {
            String colName = e.getKey();
            if (!isPrimaryKey(colName, pKeys)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                sql.append(colName).append(" = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" WHERE ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(pKeys[i]).append(" = ?");
            paras.add(ids[i]);
        }
    }

    @Override
    public void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras) {
        sql.append("ALTER TABLE ").append(table.getName()).append(" UPDATE ");
        String[] pKeys = table.getPrimaryKey();
        for (Map.Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (modifyFlag.contains(colName) && !isPrimaryKey(colName, pKeys) && table.hasColumnLabel(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                sql.append(colName).append(" = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" WHERE ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(pKeys[i]).append(" = ?");
            paras.add(attrs.get(pKeys[i]));
        }
    }


    @Override
    public String forFindByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
        StringBuilder sqlBuilder = SqlBuilder.forFindByColumns(alias, joins, table, loadColumns, columns, orderBy, ' ');

        if (limit != null) {
            sqlBuilder.append(" LIMIT " + limit);
        }

        return sqlBuilder.toString();
    }


    @Override
    public String forFindCountByColumns(String alias, List<Join> joins, String table, List<Column> columns) {
        return SqlBuilder.forFindCountByColumns(alias, joins, table, columns, ' ');
    }


    @Override
    public String forDeleteByColumns(String alias, List<Join> joins, String table, List<Column> columns) {
        StringBuilder sqlBuilder = new StringBuilder(45);
        sqlBuilder.append("ALTER TABLE ")
                .append(table)
                .append(" DELETE ");

//        SqlBuilder.buildAlias(sqlBuilder, alias);
        SqlBuilder.buildJoinSql(sqlBuilder, joins, ' ');
        SqlBuilder.buildWhereSql(sqlBuilder, columns, ' ');

        return sqlBuilder.toString();
    }


    @Override
    public String forPaginateSelect(String loadColumns) {
        return "SELECT " + loadColumns;
    }


    @Override
    public String forPaginateFrom(String alias, List<Join> joins, String table, List<Column> columns, String orderBy) {
        return SqlBuilder.forPaginateFrom(alias, joins, table, columns, orderBy, ' ');
    }

    @Override
    public String forPaginateTotalRow(String select, String sqlExceptSelect, Object ext) {
        if (ext instanceof Model) {
            String[] primaryKeys = CPI.getTable((Model) ext).getPrimaryKey();
            if (primaryKeys != null && primaryKeys.length == 1) {
                return "select count(" + primaryKeys[0] + ") " + replaceOrderBy(sqlExceptSelect);
            }
        }

        //return "select count(*) " + replaceOrderBy(sqlExceptSelect);
        return super.forPaginateTotalRow(select, sqlExceptSelect, ext);
    }
}
