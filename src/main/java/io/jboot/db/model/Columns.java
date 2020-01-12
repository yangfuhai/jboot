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
     * IS NOT NULL
     *
     * @param name
     * @return
     */
    public Columns isNotNull(String name) {
        this.add(Column.create(name, null, Column.LOGIC_IS_NOT_NULL));
        return this;
    }


    public Columns in(String name, Object... arrays) {
        this.add(Column.create(name, arrays, Column.LOGIC_IN));
        return this;
    }

    public Columns notIn(String name, Object... arrays) {
        this.add(Column.create(name, arrays, Column.LOGIC_NOT_IN));
        return this;
    }


    public Columns between(String name, Object start, Object end) {
        this.add(Column.create(name, new Object[]{start, end}, Column.LOGIC_BETWEEN));
        return this;
    }

    public Columns notBetween(String name, Object start, Object end) {
        this.add(Column.create(name, new Object[]{start, end}, Column.LOGIC_NOT_BETWEEN));
        return this;
    }


    public Columns group(Columns columns) {
        this.add(new Group(columns));
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
                    or();
                }
            }
        }
        return this;
    }


    public Columns orEqs(String name, Object... values) {
        return ors(name, Column.LOGIC_EQUALS, values);
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

    private void buildCacheKey(StringBuilder s, List<Column> columns) {
        for (Column column : columns) {
            if (column instanceof Or) {
                s.append("or").append("-");
                continue;
            }
            if (column instanceof Group) {
                s.append("(");
                buildCacheKey(s, ((Group) column).getColumns().getList());
                s.append(")-");
                continue;
            }
            s.append(column.getName())
                    .append("-")
                    .append(getLogicStr(column.getLogic()))
                    .append("-");
            Object value = column.getValue();
            if (value == null) {
                continue;
            }
            if (value.getClass().isArray()) {
                s.append(array2String((Object[]) column.getValue()));
            } else {
                s.append(column.getValue());
            }
            s.append("-");
        }
        s.deleteCharAt(s.length() - 1);
    }

    private static String array2String(Object[] a) {
        if (a == null) {
            return "null";
        }

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append("-");
        }
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
        return dialect.forFindByColumns("table", "*", getList(), null, null);
    }

    public String toSqlServerSql() {
        JbootSqlServerDialect dialect = new JbootSqlServerDialect();
        return dialect.forFindByColumns("table", "*", getList(), null, null);
    }

    @Override
    public String toString() {
        return getCacheKey();
    }

    public static void main(String[] args) {
        Columns columns = Columns.create();
        System.out.println(columns.getCacheKey());

        columns.add("name", "zhangsan");
        System.out.println(columns.getCacheKey());

        columns.ge("age", 10);
        System.out.println(columns.getCacheKey());

        columns.group(Columns.create().likeAppendPercent("name", "lisi").eq("age", 20));
        System.out.println(columns.getCacheKey());

        columns.or();

        columns.group(Columns.create().isNotNull("price").isNull("nickname").group(Columns.create().in("name", "123", "123", "111").notIn("nickname", "aaa", "bbb")));

        System.out.println(columns.getCacheKey());
        columns.or();

        columns.between("name", "123", "1233");
        System.out.println(columns.getCacheKey());

        System.out.println(Arrays.toString(columns.getValueArray()));
        System.out.println(columns.toMysqlSql());
        System.out.println(columns.toSqlServerSql());

        JbootMysqlDialect dialect = new JbootMysqlDialect();
        System.out.println(dialect.forDeleteByColumns("table", columns.getList()));
        System.out.println(dialect.forFindCountByColumns("table", columns.getList()));

    }

}
