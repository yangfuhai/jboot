package io.jboot.db.dialect;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.db.model.Column;
import io.jboot.db.model.Join;
import io.jboot.db.model.SqlBuilder;
import io.jboot.exception.JbootException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JbootInformixDialect extends Dialect implements JbootDialect {


    @Override
    public String forTableBuilderDoBuild(String tableName) {
        return "select * from " + tableName + " where 1 = 2";
    }

    @Override
    public void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras) {
        sql.append("insert into ").append(table.getName()).append('(');
        StringBuilder temp = new StringBuilder(") values(");
        for (Map.Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (table.hasColumnLabel(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                    temp.append(", ");
                }
                sql.append(colName);
                temp.append('?');
                paras.add(e.getValue());
            }
        }
        sql.append(temp).append(')');
    }

    @Override
    public String forModelDeleteById(Table table) {
        String[] pKeys = table.getPrimaryKey();
        StringBuilder sql = new StringBuilder(45);
        sql.append("delete from ");
        sql.append(table.getName());
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras) {
        sql.append("update ").append(table.getName()).append(" set ");
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
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
            paras.add(attrs.get(pKeys[i]));
        }
    }

    @Override
    public String forModelFindById(Table table, String columns) {
        StringBuilder sql = new StringBuilder("select ").append(columns).append(" from ");
        sql.append(table.getName());
        sql.append(" where ");
        String[] pKeys = table.getPrimaryKey();
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public String forDbFindById(String tableName, String[] pKeys) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        StringBuilder sql = new StringBuilder("select * from ").append(tableName).append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public String forDbDeleteById(String tableName, String[] pKeys) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        StringBuilder sql = new StringBuilder("delete from ").append(tableName).append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public void forDbSave(String tableName, String[] pKeys, Record record, StringBuilder sql, List<Object> paras) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        sql.append("insert into ");
        sql.append(tableName).append('(');
        StringBuilder temp = new StringBuilder();
        temp.append(") values(");

        for (Map.Entry<String, Object> e : record.getColumns().entrySet()) {
            if (paras.size() > 0) {
                sql.append(", ");
                temp.append(", ");
            }
            sql.append(e.getKey());
            temp.append('?');
            paras.add(e.getValue());
        }
        sql.append(temp).append(')');
    }

    @Override
    public void forDbUpdate(String tableName, String[] pKeys, Object[] ids, Record record, StringBuilder sql, List<Object> paras) {
        tableName = tableName.trim();
        trimPrimaryKeys(pKeys);

        sql.append("update ").append(tableName).append(" set ");
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
        sql.append(" where ");
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pKeys[i]).append(" = ?");
            paras.add(ids[i]);
        }
    }

    /**
     * sql.replaceFirst("(?i)select", "") 正则中带有 "(?i)" 前缀，指定在匹配时不区分大小写
     */
    @Override
    public String forPaginate(int pageNumber, int pageSize, StringBuilder findSql) {
//        int end = pageNumber * pageSize;
//        if (end <= 0) {
//            end = pageSize;
//        }

        int begin = (pageNumber - 1) * pageSize;
        if (begin < 0) {
            begin = 0;
        }

//        StringBuilder ret = new StringBuilder();
//        ret.append(String.format("select skip %s first %s ", begin + "", pageSize + ""));
//        ret.append(findSql.toString().replaceFirst("(?i)select", ""));

        StringBuilder ret = new StringBuilder("select skip ");
        ret.append(begin).append(" first ").append(pageSize);
        ret.append(findSql, 6, findSql.length());
        return ret.toString();
    }

    @Override
    public void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
        fillStatementHandleDateType(pst, paras);
    }

    @Override
    public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
        fillStatementHandleDateType(pst, paras);
    }


    //for jbootDialect -----------------
    @Override
    public String forFindByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
        StringBuilder sqlBuilder = SqlBuilder.forFindByColumns(alias, joins, table, loadColumns, columns, orderBy, ' ');
        if (limit == null) {
            return sqlBuilder.toString();
        }

        if (limit instanceof Number) {
            StringBuilder ret = new StringBuilder("select first ");
            ret.append(limit).append(" ");
            ret.append(sqlBuilder, 6, sqlBuilder.length());
            return ret.toString();
        } else if (limit instanceof String && limit.toString().contains(",")) {
            String[] startAndEnd = limit.toString().split(",");
            String start = startAndEnd[0];
            String size = startAndEnd[1];

            StringBuilder ret = new StringBuilder("select skip ");
            ret.append(start).append(" first ").append(size);
            ret.append(sqlBuilder, 6, sqlBuilder.length());
            return ret.toString();
        } else {
            throw new JbootException("sql limit is error!,limit must is Number of String like \"0,10\"");
        }

    }

    @Override
    public String forFindCountByColumns(String alias, List<Join> joins, String table, String loadColumns, List<Column> columns) {
        return SqlBuilder.forFindCountByColumns(alias, joins, table, loadColumns, columns, ' ');
    }

    @Override
    public String forDeleteByColumns(String alias, List<Join> joins, String table, List<Column> columns) {
        return SqlBuilder.forDeleteByColumns(alias, joins, table, columns, ' ');
    }

    @Override
    public String forPaginateSelect(String loadColumns) {
        return "select " + loadColumns;
    }


    @Override
    public String forPaginateFrom(String alias, List<Join> joins, String table, List<Column> columns, String orderBy) {
        return SqlBuilder.forPaginateFrom(alias, joins, table, columns, orderBy, ' ');
    }

    @Override
    public String forPaginateTotalRow(String select, String sqlExceptSelect, Object ext) {
        String distinctSql = SqlBuilder.forPaginateDistinctTotalRow(select, sqlExceptSelect, ext);
        return distinctSql != null ? distinctSql : super.forPaginateTotalRow(select, sqlExceptSelect, ext);
    }

}