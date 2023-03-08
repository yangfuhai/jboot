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

import io.jboot.db.dialect.JbootMysqlDialect;
import io.jboot.db.dialect.JbootSqlServerDialect;
import io.jboot.utils.StrUtil;

import java.io.Serializable;
import java.util.*;

/**
 * Column 的工具类，用于方便组装sql
 */
public class Columns implements Serializable {

    public static final Columns EMPTY = Columns.create();

    private List<Column> cols;

    /**
     * 在很多场景下，只会根据字段来查询，如果字段值为 null 的情况，Columns 会直接忽略 null 值，此时会造成结果不准确的情况
     * <p>
     * 比如 ：
     * ```
     * public ShopInfo findFirstByAccountId(BigInteger accountId) {
     * return findFirstByColumns(Columns.create("account_id", accountId));
     * }
     * ```
     * 根据账户 id 来查询该账户对应的 ShopInfo，此时 如果传入 null 值，则返回了 第一个 ShopInfo，这个 ShopInfo 可能并不是该账户的。
     * <p>
     * 在这种场景下，我们就不应该允许用户传入 null 值进行查询，当传入 null 的时候直接抛出异常即可 。
     * <p>
     * 此时，我们可以使用如下代码进行查询。
     * <p>
     * ```
     * public ShopInfo findFirstByAccountId(BigInteger accountId) {
     * return findFirstByColumns(Columns.safeMode().eq("account_id", accountId));
     * }
     * ```
     */
    private boolean useSafeMode = false;


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


    public static Columns safeMode() {
        return new Columns().useSafeMode();
    }


    public static Columns safeCreate(String name, Object value) {
        return safeMode().eq(name, value);
    }


    /**
     * add new column in Columns
     *
     * @param column
     */
    public Columns add(Column column) {

        //do not add null value column
        if (column.hasPara() && column.getValue() == null) {
            return this;
        }

        if (this.cols == null) {
            this.cols = new LinkedList<>();
        }

        this.cols.add(column);
        return this;
    }


    /**
     * add new column in Columns
     *
     * @param column
     */
    public Columns addToFirst(Column column) {

        //do not add null value column
        if (column.hasPara() && column.getValue() == null) {
            return this;
        }

        if (this.cols == null) {
            this.cols = new LinkedList<>();
        }

        this.cols.add(0, column);
        return this;
    }


    /**
     * add Columns
     *
     * @param columns
     * @return
     */
    public Columns add(Columns columns) {
        return append(columns);
    }


    /**
     * add Columns To First
     *
     * @param columns
     * @return
     */
    public Columns addToFirst(Columns columns) {
        if (columns != null && !columns.isEmpty()) {
            for (Column column : columns.getList()) {
                addToFirst(column);
            }
        }
        return this;
    }


    /**
     * equals
     *
     * @param name
     * @param value
     * @return
     */
    public Columns eq(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value));
    }

    /**
     * not equals !=
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ne(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_NOT_EQUALS));
    }


    /**
     * like
     *
     * @param name
     * @param value
     * @return
     */
    public Columns like(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_LIKE));
    }

    /**
     * 自动添加两边 % 的like
     *
     * @param name
     * @param value
     * @return
     */
    public Columns likeAppendPercent(String name, Object value) {
        Util.checkNullParas(this, name, value);
        if (value == null || (value instanceof String && StrUtil.isBlank((String) value))) {
            return this;
        }
        return add(Column.create(name, "%" + value + "%", Column.LOGIC_LIKE));
    }

    /**
     * 大于 great than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns gt(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_GT));
    }

    /**
     * 大于等于 great or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns ge(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_GE));
    }

    /**
     * 小于 less than
     *
     * @param name
     * @param value
     * @return
     */
    public Columns lt(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_LT));
    }

    /**
     * 小于等于 less or equal
     *
     * @param name
     * @param value
     * @return
     */
    public Columns le(String name, Object value) {
        Util.checkNullParas(this, name, value);
        return add(Column.create(name, value, Column.LOGIC_LE));
    }


    /**
     * IS NULL
     *
     * @param name
     * @return
     */
    public Columns isNull(String name) {
        return add(Column.create(name, null, Column.LOGIC_IS_NULL));
    }


    /**
     * @param name
     * @param condition
     * @return
     */
    public Columns isNullIf(String name, Boolean condition) {
        if (condition != null && condition) {
            add(Column.create(name, null, Column.LOGIC_IS_NULL));
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
        return add(Column.create(name, null, Column.LOGIC_IS_NOT_NULL));
    }


    /**
     * IS NOT NULL
     *
     * @param name
     * @param condition
     * @return
     */
    public Columns isNotNullIf(String name, Boolean condition) {
        if (condition != null && condition) {
            add(Column.create(name, null, Column.LOGIC_IS_NOT_NULL));
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
        Util.checkNullParas(this, name, arrays);

        //忽略 columns.in("name", null) 的情况
        if (arrays != null && arrays.length == 1 && arrays[0] == null) {
            return this;
        }
        return add(Column.create(name, arrays, Column.LOGIC_IN));
    }


    /**
     * in Collection
     *
     * @param name
     * @param collection
     * @return
     */
    public Columns in(String name, Collection<?> collection) {
        Util.checkNullParas(this, collection);
        if (collection != null && !collection.isEmpty()) {
            in(name, collection.toArray());
        }
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
        Util.checkNullParas(this, name, arrays);

        //忽略 columns.notIn("name", null) 的情况
        if (arrays != null && arrays.length == 1 && arrays[0] == null) {
            return this;
        }
        return add(Column.create(name, arrays, Column.LOGIC_NOT_IN));
    }


    /**
     * not in Collection
     *
     * @param name
     * @param collection
     * @return
     */
    public Columns notIn(String name, Collection<?> collection) {
        Util.checkNullParas(this, collection);
        if (collection != null && !collection.isEmpty()) {
            notIn(name, collection.toArray());
        }
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
        Util.checkNullParas(this, name, start, end);
        return add(Column.create(name, new Object[]{start, end}, Column.LOGIC_BETWEEN));
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
        Util.checkNullParas(this, name, start, end);
        return add(Column.create(name, new Object[]{start, end}, Column.LOGIC_NOT_BETWEEN));
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
            add(new Group(columns));
        }
        return this;
    }


    /**
     * @param columns
     * @param condition
     * @return
     */
    public Columns groupIf(Columns columns, Boolean condition) {
        if (columns == this) {
            throw new IllegalArgumentException("Columns.group(...) need a new Columns");
        }
        if (condition != null && condition && !columns.isEmpty()) {
            add(new Group(columns));
        }
        return this;
    }

    /**
     * @param name
     * @return
     */
    public Columns groupBy(String name) {
        add(new GroupBy(name));
        return this;
    }

    /**
     * @param name
     * @return
     */
    public Columns having(String name) {
        add(new Having(name));
        return this;
    }


    /**
     * @param sql
     * @return
     */
    public Columns having(String sql, Object... paras) {
        add(new Having(sql, paras));
        return this;
    }


    /**
     * @param columns
     * @return
     */
    public Columns having(Columns columns) {
        add(new Having(columns));
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
            add(new SqlPart(sql));
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
        Util.checkNullParas(this, paras);
        if (StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql, paras));
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
    public Columns sqlPartIf(String sql, Boolean condition) {
        if (condition != null && condition && StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql));
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
    public Columns sqlPartIf(String sql, Boolean condition, Object... paras) {
        Util.checkNullParas(this, paras);
        if (condition != null && condition && StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql, paras));
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
            add(new SqlPart(sql, true));
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
        Util.checkNullParas(this, paras);
        if (StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql, paras, true));
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
    public Columns sqlPartWithoutLinkIf(String sql, Boolean condition) {
        if (condition != null && condition && StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql, true));
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
    public Columns sqlPartWithoutLinkIf(String sql, Boolean condition, Object... paras) {
        Util.checkNullParas(this, paras);
        if (condition != null && condition && StrUtil.isNotBlank(sql)) {
            add(new SqlPart(sql, paras, true));
        }
        return this;
    }


    public Columns or() {
        add(new Or());
        return this;
    }


    public Columns ors(String name, String logic, Object... values) {
        Util.checkNullParas(this, name, values);

        Columns columns = new Columns();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value != null) {
                columns.add(Column.create(name, value, logic));
                if (i != values.length - 1) {
                    columns.add(new Or());
                }
            }
        }

        return group(columns);
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
    public Columns appendIf(Columns columns, Boolean condition) {
        if (condition != null && condition) {
            append(columns);
        }
        return this;
    }

    public boolean isUseSafeMode() {
        return useSafeMode;
    }

    public Columns useSafeMode() {
        this.useSafeMode = true;
        return this;
    }

    public Columns unUseSafeMode() {
        this.useSafeMode = false;
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

    public boolean containsName(String name) {
        if (isEmpty()) {
            return false;
        }

        for (Column col : cols) {
            if (col.getName() != null && col.getName().equals(name)) {
                return true;
            }
        }
        return false;
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
                        .append(getLogicString(column.getLogic()))
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
    private static String getLogicString(String logic) {
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
     * 输出 where 后面的 sql 部分，风格是 mysql 的风格 SQL
     *
     * @return
     */
    public String toWherePartSql() {
        return toWherePartSql('`', false);
    }


    /**
     * 输出 where 后面的 sql 部分，风格是 mysql 的风格 SQL
     *
     * @param withWhereKeyword 是否带上 where 关键字
     * @return
     */
    public String toWherePartSql(boolean withWhereKeyword) {
        return toWherePartSql('`', withWhereKeyword);
    }


    /**
     * 输出 where 部分的 sql
     *
     * @param separator        字段分隔符
     * @param withWhereKeyword 是否带上 "where 关键字"
     * @return
     */
    public String toWherePartSql(char separator, boolean withWhereKeyword) {
        StringBuilder sb = new StringBuilder();
        SqlBuilder.buildWhereSql(sb, getList(), separator, withWhereKeyword);
        return sb.toString();
    }


    @Override
    public String toString() {
        String cacheKey = getCacheKey();
        return StrUtil.isNotBlank(cacheKey) ? cacheKey : "{}";
    }


    public static void main(String[] args) {

        Columns columns = Columns.create().useSafeMode().or().or().or().eq("aa", "bb").or().or().or().notIn("aaa", 123, 456, 789).like("titile", "a");
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
                .eq("age", "18").eq("ddd", null));

        columns.or();
        columns.or();

        columns.group(Columns.create().or().or().sqlPart("name = ?", "zhangsan"));
        columns.or();
        columns.or();
        columns.or();

        columns.between("name", "123", "1233");
        columns.between("name", "123", "1233");
        columns.or();

//        columns.sqlPartWithoutLink("group by xxx");
        columns.groupBy("aaa").having(Columns.create("aaa", "bbb").ge("ccc", 111));
//        columns.or();
//        columns.or();
//        columns.or();

        System.out.println(columns.getCacheKey());
        System.out.println(Arrays.toString(columns.getValueArray()));
        System.out.println(columns.toMysqlSql());
        System.out.println("-----------");
        System.out.println(columns.toWherePartSql('"', true));

    }

    /**
     * 这个只是用于调试
     *
     * @return
     */
    private String toMysqlSql() {
        JbootMysqlDialect dialect = new JbootMysqlDialect();
        return dialect.forFindByColumns(null, null, "table", "*", getList(), null, null);
    }


    /**
     * 这个只是用于调试
     *
     * @return
     */
    private String toSqlServerSql() {
        JbootSqlServerDialect dialect = new JbootSqlServerDialect();
        return dialect.forFindByColumns(null, null, "table", "*", getList(), null, null);
    }

}
