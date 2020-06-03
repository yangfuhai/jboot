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

import io.jboot.db.dialect.JbootMysqlDialect;
import io.jboot.db.dialect.JbootSqlServerDialect;
import io.jboot.utils.StrUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Column 的工具类，用于方便组装sql
 */
public class Columns implements Serializable {

    public static final Columns EMPTY = Columns.create();

    private List<Column> cols;


    public static Columns create() {
        return new Columns();
    }

    public static Columns create(Column column) {
        Columns that = new Columns();
        that.add(column);
        return that;

    }

    public static Columns create(List<Column> columns) {
        Columns that = new Columns();
        that.cols = columns;
        return that;

    }

    public static Columns create(String name, Object value) {
        return create().eq(name, value);
    }


    /**
     * add new column in Columns
     *
     * @param column
     */
    public void add(Column column) {

        //do not add null value column
        if (column.hasPara() && column.getValue() == null) {
            return;
        }

        if (this.cols == null) {
            this.cols = new LinkedList<>();
        }

        this.cols.add(column);
    }


    public Columns add(String name, Object value) {
        return eq(name, value);
    }


    /**
     * equals
     *
     * @param name
     * @param value
     * @return
     */
    public Columns eq(String name, Object value) {
        this.add(Column.create(name, value));
        return this;
    }

    /**
     * not equals !=
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ne(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_NOT_EQUALS));
        return this;
    }


    /**
     * like
     *
     * @param name
     * @param value
     * @return
     */
    public Columns like(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_LIKE));
        return this;
    }

    /**
     * 自动添加两边 % 的like
     *
     * @param name
     * @param value
     * @return
     */
    public Columns likeAppendPercent(String name, Object value) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            //do nothing
            return this;
        }
        this.add(Column.create(name, "%" + value + "%", Column.LOGIC_LIKE));
        return this;
    }

    /**
     * 大于 great than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns gt(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_GT));
        return this;
    }

    /**
     * 大于等于 great or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ge(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_GE));
        return this;
    }

    /**
     * 小于 less than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns lt(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_LT));
        return this;
    }

    /**
     * 小于等于 less or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns le(String name, Object value) {
        this.add(Column.create(name, value, Column.LOGIC_LE));
        return this;
    }


    /**
     * IS NULL
     *
     * @param name
     * @return
     */
    public Columns isNull(String name) {
        this.add(Column.create(name, null, Column.LOGIC_IS_NULL));
        return this;
    }


    /**
     * @param name
     * @param condition
     * @return
     */
    public Columns isNullIf(String name, boolean condition) {
        if (condition) {
            this.add(Column.create(name, null, Column.LOGIC_IS_NULL));
        }
        return this;
    }


    /**
     * IS NOT NULL
     *
     * @param name
     * @return
     */
    public Columns isNotNull(String name) {
        this.add(Column.create(name, null, Column.LOGIC_IS_NOT_NULL));
        return this;
    }


    /**
     * IS NOT NULL
     *
     * @param name
     * @param condition
     * @return
     */
    public Columns isNotNullIf(String name, boolean condition) {
        if (condition) {
            this.add(Column.create(name, null, Column.LOGIC_IS_NOT_NULL));
        }
        return this;
    }


    /**
     * in arrays
     *
     * @param name
     * @param arrays
     * @return
     */
    public Columns in(String name, Object... arrays) {
        this.add(Column.create(name, arrays, Column.LOGIC_IN));
        return this;
    }

    /**
     * not int arrays
     *
     * @param name
     * @param arrays
     * @return
     */
    public Columns notIn(String name, Object... arrays) {
        this.add(Column.create(name, arrays, Column.LOGIC_NOT_IN));
        return this;
    }


    /**
     * between
     *
     * @param name
     * @param start
     * @param end
     * @return
     */
    public Columns between(String name, Object start, Object end) {
        this.add(Column.create(name, new Object[]{start, end}, Column.LOGIC_BETWEEN));
        return this;
    }

    /**
     * not between
     *
     * @param name
     * @param start
     * @param end
     * @return
     */
    public Columns notBetween(String name, Object start, Object end) {
        this.add(Column.create(name, new Object[]{start, end}, Column.LOGIC_NOT_BETWEEN));
        return this;
    }


    /**
     * group
     *
     * @param columns
     * @return
     */
    public Columns group(Columns columns) {
        if (columns == this) {
            throw new IllegalArgumentException("Columns.group(...) need a new Columns");
        }
        if (!columns.isEmpty()) {
            this.add(new Group(columns));
        }
        return this;
    }


    /**
     * @param columns
     * @param conditon
     * @return
     */
    public Columns groupIf(Columns columns, boolean conditon) {
        if (columns == this) {
            throw new IllegalArgumentException("Columns.group(...) need a new Columns");
        }
        if (conditon && !columns.isEmpty()) {
            this.add(new Group(columns));
        }
        return this;
    }


    /**
     * customize string sql
     *
     * @param sql
     * @return
     */
    public Columns sqlPart(String sql) {
        if (StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param paras
     * @return
     */
    public Columns sqlPart(String sql, Object... paras) {
        if (StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, paras));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param condition
     * @return
     */
    public Columns sqlPartIf(String sql, boolean condition) {
        if (condition && StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param condition
     * @param paras
     * @return
     */
    public Columns sqlPartIf(String sql, boolean condition, Object... paras) {
        if (condition && StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, paras));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @return
     */
    public Columns sqlPartWithoutLink(String sql) {
        if (StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, true));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param paras
     * @return
     */
    public Columns sqlPartWithoutLink(String sql, Object... paras) {
        if (StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, paras, true));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param condition
     * @return
     */
    public Columns sqlPartWithoutLinkIf(String sql, boolean condition) {
        if (condition && StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, true));
        }
        return this;
    }

    /**
     * customize string sql
     *
     * @param sql
     * @param condition
     * @param paras
     * @return
     */
    public Columns sqlPartWithoutLinkIf(String sql, boolean condition, Object... paras) {
        if (condition && StrUtil.isNotBlank(sql)) {
            this.add(new SqlPart(sql, paras, true));
        }
        return this;
    }


    public Columns or() {
        this.add(new Or());
        return this;
    }


    public Columns ors(String name, String logic, Object... values) {
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value != null) {
                this.add(Column.create(name, value, logic));
                if (i != values.length - 1) {
                    this.add(new Or());
                }
            }
        }
        return this;
    }


    public Columns orEqs(String name, Object... values) {
        return ors(name, Column.LOGIC_EQUALS, values);
    }


    /**
     * 追加 新的 columns
     *
     * @param columns
     * @return
     */
    public Columns append(Columns columns) {
        if (columns != null && !columns.isEmpty()) {
            for (Column column : columns.getList()) {
                add(column);
            }
        }
        return this;
    }


    /**
     * 追加 新的 columns
     *
     * @param columns
     * @return
     */
    public Columns appendIf(Columns columns, boolean condition) {
        if (condition) {
            append(columns);
        }
        return this;
    }


    public boolean isEmpty() {
        return cols == null || cols.isEmpty();
    }


    public Object[] getValueArray() {
        return Util.getValueArray(cols);
    }


    public List<Column> getList() {
        return cols;
    }


    public String getCacheKey() {
        if (isEmpty()) {
            return null;
        }

        List<Column> columns = new ArrayList<>(cols);
        StringBuilder s = new StringBuilder();
        buildCacheKey(s, columns);

        return s.toString();
    }

    private static final char SQL_CACHE_SEPARATOR = '-';

    private void buildCacheKey(StringBuilder s, List<Column> columns) {
        for (int i = 0; i < columns.size(); i++) {

            Column column = columns.get(i);

            if (column instanceof Or) {
                Column before = i > 0 ? columns.get(i - 1) : null;
                if (before != null && !(before instanceof Or)) {
                    s.append("or").append(SQL_CACHE_SEPARATOR);
                }
            } else if (column instanceof Group) {
                s.append('(');
                buildCacheKey(s, ((Group) column).getColumns().getList());
                s.append(')').append(SQL_CACHE_SEPARATOR);
            } else if (column instanceof SqlPart) {
                String sqlpart = ((SqlPart) column).getSql();
                Object value = column.getValue();
                if (value != null) {
                    if (value.getClass().isArray()) {
                        Object[] values = (Object[]) value;
                        for (Object v : values) {
                            sqlpart = Util.replaceSqlPara(sqlpart, v);
                        }
                    } else {
                        sqlpart = Util.replaceSqlPara(sqlpart, value);
                    }
                }
                s.append(Util.deleteWhitespace(sqlpart)).append(SQL_CACHE_SEPARATOR);
            } else {
                s.append(column.getName())
                        .append(SQL_CACHE_SEPARATOR)
                        .append(getLogicStr(column.getLogic()))
                        .append(SQL_CACHE_SEPARATOR);
                Object value = column.getValue();
                if (value != null) {
                    if (value.getClass().isArray()) {
                        s.append(Util.array2String((Object[]) value));
                    } else {
                        s.append(column.getValue());
                    }
                    s.append(SQL_CACHE_SEPARATOR);
                }
            }
        }
        s.deleteCharAt(s.length() - 1);
    }


    /**
     * @param logic
     * @return
     */
    private String getLogicStr(String logic) {
        switch (logic) {
            case Column.LOGIC_LIKE:
                return "lk";
            case Column.LOGIC_GT:
                return "gt";
            case Column.LOGIC_GE:
                return "ge";
            case Column.LOGIC_LT:
                return "lt";
            case Column.LOGIC_LE:
                return "le";
            case Column.LOGIC_EQUALS:
                return "eq";
            case Column.LOGIC_NOT_EQUALS:
                return "neq";
            case Column.LOGIC_IS_NULL:
                return "isn";
            case Column.LOGIC_IS_NOT_NULL:
                return "nn";
            case Column.LOGIC_IN:
                return "in";
            case Column.LOGIC_NOT_IN:
                return "nin";
            case Column.LOGIC_BETWEEN:
                return "bt";
            case Column.LOGIC_NOT_BETWEEN:
                return "nbt";
            default:
                return "";
        }
    }

    /**
     * 这个只是用于调试
     *
     * @return
     */
    public String toMysqlSql() {
        JbootMysqlDialect dialect = new JbootMysqlDialect();
        return dialect.forFindByColumns(null, "table", "*", getList(), null, null);
    }

    public String toSqlServerSql() {
        JbootSqlServerDialect dialect = new JbootSqlServerDialect();
        return dialect.forFindByColumns(null, "table", "*", getList(), null, null);
    }

    @Override
    public String toString() {
        return getCacheKey();
    }


    public static void main(String[] args) {

        Columns columns = Columns.create().or().or().or().eq("aa", "bb").or().or().or().notIn("aaa", 123, 456, 789).like("titile", "a");
        columns.group(Columns.create().or().or().sqlPart("aa=bb"));
        columns.group(Columns.create("aa", "bb").eq("cc", "dd")
                .group(Columns.create("aa", "bb").eq("cc", "dd"))
                .group(Columns.create("aa", "bb").eq("cc", "dd").group(Columns.create("aa", "bb").eq("cc", "dd"))));

        columns.ge("age", 10);
        columns.or();
        columns.or();
        columns.or();
        columns.or();
        columns.sqlPart("user.id != ? and xxx= ?", 1, "abc2");
        columns.sqlPart("user.id != ? and xxx= ?", 1, "abc2");

        columns.or();
        columns.or();
        columns.or();
        columns.group(Columns.create().likeAppendPercent("name", "null").or().or().or()
                .eq("age", "18").eq("ddd", "ddd"));

        columns.or();
        columns.or();

        columns.group(Columns.create().or().or().sqlPart("name = ?", "zhangsan"));
        columns.or();
        columns.or();
        columns.or();

        columns.between("name", "123", "1233");
        columns.between("name", "123", "1233");
        columns.or();

        columns.sqlPartWithoutLink("group by xxx");
        columns.or();
        columns.or();
        columns.or();

        System.out.println(columns.getCacheKey());
        System.out.println(Arrays.toString(columns.getValueArray()));
        System.out.println(columns.toMysqlSql());

    }

}
