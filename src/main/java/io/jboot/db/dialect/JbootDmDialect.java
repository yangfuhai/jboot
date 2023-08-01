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
package io.jboot.db.dialect;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import io.jboot.db.model.Column;
import io.jboot.db.model.Join;
import io.jboot.db.model.SqlBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 达梦数据库的数据方言
 */
public class JbootDmDialect extends OracleDialect implements JbootDialect {

    private static final char separator = '"';

    public String wrap(String wrap) {
        return "\"" + wrap.toUpperCase() + "\"";
    }

    @Override
    public String forTableBuilderDoBuild(String tableName) {
        return toUpperCase("select * from " + wrap(tableName) + " where rownum < 1");
    }


    @Override
    // insert into table (id,name) values(seq.nextval, ？)
    public void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras) {
        sql.append("insert into ").append(wrap(table.getName())).append('(');
        StringBuilder temp = new StringBuilder(") values(");
        String[] pKeys = table.getPrimaryKey();
        int count = 0;
        for (Map.Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (table.hasColumnLabel(colName)) {
                Object value = e.getValue();
                if (isPrimaryKey(colName, pKeys) && value == null) {
                    continue;
                }

                if (count++ > 0) {
                    sql.append(", ");
                    temp.append(", ");
                }


                if (isPrimaryKey(colName, pKeys) && value instanceof String && ((String) value).endsWith(".nextval")) {
                    sql.append(wrap(colName));
                    temp.append(value);
                } else {
                    sql.append(wrap(colName));
                    temp.append('?');
                    paras.add(value);
                }
            }
        }
        sql.append(temp).append(')');
    }

    @Override
    public String forModelDeleteById(Table table) {
        String[] pKeys = table.getPrimaryKey();
        StringBuilder sql = new StringBuilder(45);
        sql.append("delete from ");
        sql.append(wrap(table.getName()));
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras) {
        sql.append("update ").append(wrap(table.getName())).append(" set ");
        String[] pKeys = table.getPrimaryKey();
        for (Map.Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (modifyFlag.contains(colName) && !isPrimaryKey(colName, pKeys) && table.hasColumnLabel(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                sql.append(wrap(colName)).append(" = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
            paras.add(attrs.get(pKeys[i]));
        }
    }


    @Override
    public String forModelFindById(Table table, String columns) {
        StringBuilder sql = new StringBuilder("select ").append(columns).append(" from ");
        sql.append(wrap(table.getName()));
        sql.append(" where ");
        String[] pKeys = table.getPrimaryKey();
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
        }
        return sql.toString();
    }


    @Override
    public String forDbFindById(String tableName, String[] pKeys) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        StringBuilder sql = new StringBuilder("select * from ").append(wrap(tableName)).append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public String forDbDeleteById(String tableName, String[] pKeys) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        StringBuilder sql = new StringBuilder("delete from ").append(wrap(tableName)).append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
        }
        return sql.toString();
    }


    @Override
    public void forDbSave(String tableName, String[] pKeys, Record record, StringBuilder sql, List<Object> paras) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        sql.append("insert into ");
        sql.append(wrap(tableName)).append('(');
        StringBuilder temp = new StringBuilder();
        temp.append(") values(");

        int count = 0;
        for (Map.Entry<String, Object> e : record.getColumns().entrySet()) {

            String colName = e.getKey();
            Object value = e.getValue();

            if (isPrimaryKey(colName, pKeys) && value == null) {
                continue;
            }

            if (count++ > 0) {
                sql.append(", ");
                temp.append(", ");
            }
            sql.append(wrap(colName));

            if (value instanceof String && isPrimaryKey(colName, pKeys) && ((String) value).endsWith(".nextval")) {
                temp.append(value);
            } else {
                temp.append('?');
                paras.add(value);
            }
        }
        sql.append(temp).append(')');
    }


    @Override
    public void forDbUpdate(String tableName, String[] pKeys, Object[] ids, Record record, StringBuilder sql, List<Object> paras) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        // Record 新增支持 modifyFlag
        Set<String> modifyFlag = CPI.getModifyFlag(record);

        sql.append("update ").append(wrap(tableName)).append(" set ");
        for (Map.Entry<String, Object> e : record.getColumns().entrySet()) {
            String colName = e.getKey();
            if (modifyFlag.contains(colName) && !isPrimaryKey(colName, pKeys)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                sql.append(wrap(colName)).append(" = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(wrap(pKeys[i])).append(" = ?");
            paras.add(ids[i]);
        }
    }

    @Override
    public String forPaginate(int pageNumber, int pageSize, StringBuilder findSql) {
        int start = (pageNumber - 1) * pageSize;
        int end = pageNumber * pageSize;
        StringBuilder ret = new StringBuilder();
        ret.append("select * from ( select row_.*, rownum rownum_ from (  ");
        ret.append(findSql);
        ret.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
        ret.append(" where table_alias.rownum_ > ").append(start);
        return ret.toString();
    }


    /////////////////jboot////////////

    @Override
    public String forFindByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
        StringBuilder sqlBuilder = SqlBuilder.forFindByColumns(alias, joins, table, loadColumns, columns, orderBy, separator);

        if (limit != null) {
            sqlBuilder.append(" LIMIT " + limit);
        }
        return toUpperCase(sqlBuilder.toString());
    }

    @Override
    public String forFindCountByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns) {
        return toUpperCase(SqlBuilder.forFindCountByColumns(alias, joins, table, loadColumns, columns, separator));
    }

    @Override
    public String forDeleteByColumns(String alias, List<Join> joins, String table, List<Column> columns) {
        return toUpperCase(SqlBuilder.forDeleteByColumns(alias, joins, table, columns, separator));
    }

    @Override
    public String forPaginateSelect(String loadColumns) {
        return toUpperCase("SELECT " + loadColumns);
    }


    @Override
    public String forPaginateFrom(String alias, List<Join> joins, String table, List<Column> columns, String orderBy) {
        return toUpperCase(SqlBuilder.forPaginateFrom(alias, joins, table, columns, orderBy, separator));
    }

    @Override
    public String forPaginateTotalRow(String select, String sqlExceptSelect, Object ext) {
        String distinctSql = SqlBuilder.forPaginateDistinctTotalRow(select, sqlExceptSelect, ext);
        return toUpperCase(distinctSql != null ? distinctSql : super.forPaginateTotalRow(select, sqlExceptSelect, ext));
    }

    public  String toUpperCase(String sql) {
        return sql.toUpperCase();
    }
}
