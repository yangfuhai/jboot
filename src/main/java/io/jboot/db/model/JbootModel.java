/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.Jboot;
import io.jboot.db.dialect.IJbootModelDialect;
import io.jboot.exception.JbootAssert;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;

import java.util.*;


@SuppressWarnings("serial")
public class JbootModel<M extends JbootModel<M>> extends Model<M> {

    private static final String COLUMN_CREATED = "created";
    private static final String COLUMN_MODIFIED = "modified";

    /**
     * 是否启用自动缓存
     */
    private boolean autoCache = true;


    /**
     * 添加数据到缓存
     *
     * @param key
     * @param value
     */
    public void putCache(Object key, Object value) {
        Jboot.me().getCache().put(tableName(), key, value);
    }

    /**
     * 获取缓存中的数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getCache(Object key) {
        return Jboot.me().getCache().get(tableName(), key);
    }

    /**
     * 获取缓存中的数据 ， 如果缓存不存在，则通过dataloader 去加载
     *
     * @param key
     * @param dataloader
     * @param <T>
     * @return
     */
    public <T> T getCache(Object key, IDataLoader dataloader) {
        return Jboot.me().getCache().get(tableName(), key, dataloader);
    }

    /**
     * 移除缓存数据
     *
     * @param key
     */
    public void removeCache(Object key) {
        if (key == null) return;
        Jboot.me().getCache().remove(tableName(), key);
    }


    /**
     * 复制一个新的model
     * 主要是用在 从缓存取出数据的时候，如果直接修改，在ehcache会抛异常
     * 如果要对model进行修改，可以先copy一份新的，然后再修改
     *
     * @return
     */
    public M copy() {
        M m = null;
        try {
            m = (M) getUsefulClass().newInstance();
            m._setAttrs(this._getAttrs());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return m;
    }


    /**
     * 可以再DAO中调用此方法使用proxy数据源进行连接数据库
     * 例如：DAO.useProxy().findById("123")
     * 注意：使用此方法，需要配置名称为 proxy 的数据源
     *
     * @return
     */
    public M useProxy() {
        M proxy = get("__proxy__");
        if (proxy != null) {
            return proxy;
        }

        proxy = copy().use("proxy").autoCache(this.autoCache);

        if (proxy._getConfig() == null) {
            proxy.use(null);
        }

        set("__proxy__", proxy);
        return proxy;
    }


    /**
     * 同 useProxy
     *
     * @return
     */
    public M useSlave() {
        M proxy = get("__slave__");
        if (proxy != null) {
            return proxy;
        }

        proxy = copy().use("slave").autoCache(this.autoCache);

        if (proxy._getConfig() == null) {
            proxy.use(null);
        }

        set("__slave__", proxy);
        return proxy;
    }

    /**
     * 同 useProxy
     *
     * @return
     */
    public M useMaster() {
        M proxy = get("__master__");
        if (proxy != null) {
            return proxy;
        }

        proxy = copy().use("master").autoCache(this.autoCache);

        if (proxy._getConfig() == null) {
            proxy.use(null);
        }

        set("__master__", proxy);
        return proxy;
    }


    /**
     * 是否启用自动缓存
     *
     * @param autoCache
     * @return
     */
    public M autoCache(boolean autoCache) {
        this.autoCache = autoCache;
        return (M) this;
    }


    /**
     * 更新或者保存
     * 有主键就更新，没有就保存
     *
     * @return
     */
    public boolean saveOrUpdate() {
        if (null == get(getPrimaryKey())) {
            return this.save();
        }
        return this.update();
    }


    /**
     * 保存数据
     *
     * @return
     */
    @Override
    public boolean save() {
        if (hasColumn(COLUMN_CREATED) && get(COLUMN_CREATED) == null) {
            set(COLUMN_CREATED, new Date());
        }
        if (null == get(getPrimaryKey()) && String.class == getPrimaryType()) {
            set(getPrimaryKey(), StringUtils.uuid());
        }
        boolean saved = super.save();
        if (saved) {
            Jboot.sendEvent(addAction(), this);
        }
        return saved;
    }


    /**
     * 删除
     *
     * @return
     */
    @Override
    public boolean delete() {
        boolean deleted = super.delete();
        if (deleted) {
            if (autoCache) {
                removeCache(get(getPrimaryKey()));
            }
            Jboot.sendEvent(deleteAction(), this);
        }
        return deleted;
    }


    /**
     * 根据ID删除
     *
     * @param idValue the id value of the model
     * @return
     */
    @Override
    public boolean deleteById(Object idValue) {
        JbootModel<?> model = findById(idValue);
        return model.delete();
    }


    /**
     * 更新
     *
     * @return
     */
    @Override
    public boolean update() {
        Boolean fromCopier = getBoolean(ModelCopier.MODEL_FROM_COPIER);
        if (fromCopier != null && fromCopier) {
            keepCopier();
        }

        if (hasColumn(COLUMN_MODIFIED)) {
            set(COLUMN_MODIFIED, new Date());
        }

        boolean update = super.update();
        if (update) {
            Object id = get(getPrimaryKey());
            if (autoCache) {
                removeCache(id);
            }
            Jboot.sendEvent(updateAction(), findById(id));
        }
        return update;
    }

    protected String addAction() {
        return tableName() + ":add";
    }

    protected String deleteAction() {
        return tableName() + ":delete";
    }

    protected String updateAction() {
        return tableName() + ":update";
    }

    /**
     * 根据ID查找model
     *
     * @param idValue the id value of the model
     * @return
     */
    @Override
    public M findById(final Object idValue) {
        return autoCache ? getCache(idValue, new IDataLoader() {
            @Override
            public Object load() {
                return findByIdWithoutCache(idValue);
            }
        }) : findByIdWithoutCache(idValue);
    }


    public M findByIdWithoutCache(Object idValue) {
        return super.findById(idValue);
    }


    private IJbootModelDialect getDialect() {
        return (IJbootModelDialect) _getConfig().getDialect();
    }

    /**
     * 根据列名和值，查找1条数据
     *
     * @param column
     * @param value
     * @return
     */
    public M findFirstByColumn(String column, Object value) {
        String sql = getDialect().forFindByColumns(tableName(), "*", Columns.create(column, value).getList(), null, 1);
        return findFirst(sql, value);
    }

    /**
     * 根据 列和值 查询1条数据
     *
     * @param column
     * @return
     */
    public M findFirstByColumn(Column column) {
        String sql = getDialect().forFindByColumns(tableName(), "*", Columns.create(column).getList(), null, 1);
        return findFirst(sql, column.getValue());
    }

    /**
     * 根据 多列和值，查询1条数据
     *
     * @param columns
     * @return
     */
    public M findFirstByColumns(Columns columns) {
        String sql = getDialect().forFindByColumns(tableName(), "*", columns.getList(), null, 1);
        LinkedList<Object> params = new LinkedList<Object>();

        if (ArrayUtils.isNotEmpty(columns.getList())) {
            for (Column column : columns.getList()) {
                params.add(column.getValue());
            }
        }
        return findFirst(sql, params.toArray());
    }


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<M> findAll() {
        String sql = getDialect().forFindByColumns(tableName(), "*", null, null, null);
        return find(sql);
    }

    /**
     * 根据列名和值 查询一个列表
     *
     * @param column
     * @param value
     * @param count  最多查询多少条数据
     * @return
     */
    public List<M> findListByColumn(String column, Object value, Integer count) {
        List<Column> columns = new ArrayList<>();
        columns.add(Column.create(column, value));
        return findListByColumns(columns, count);
    }


    /**
     * 根据 列信息 查找数据列表
     *
     * @param column
     * @param count
     * @return
     */
    public List<M> findListByColumn(Column column, Integer count) {
        return findListByColumns(Columns.create(column).getList(), count);
    }


    public List<M> findListByColumn(String column, Object value) {
        return findListByColumn(column, value, null);
    }

    public List<M> findListByColumn(Column column) {
        return findListByColumn(column, null);
    }

    public List<M> findListByColumns(List<Column> columns) {
        return findListByColumns(columns, null, null);
    }

    public List<M> findListByColumns(List<Column> columns, String orderBy) {
        return findListByColumns(columns, orderBy, null);
    }

    public List<M> findListByColumns(List<Column> columns, Integer count) {
        return findListByColumns(columns, null, count);
    }


    public List<M> findListByColumns(Columns columns) {
        return findListByColumns(columns.getList());
    }

    public List<M> findListByColumns(Columns columns, String orderBy) {
        return findListByColumns(columns.getList(), orderBy);
    }

    public List<M> findListByColumns(Columns columns, Integer count) {
        return findListByColumns(columns.getList(), count);
    }


    public List<M> findListByColumns(Columns columns, String orderBy, Integer count) {
        return findListByColumns(columns.getList(), orderBy, count);
    }


    /**
     * 根据列信心查询列表
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<M> findListByColumns(List<Column> columns, String orderBy, Integer count) {
        LinkedList<Object> params = new LinkedList<Object>();

        if (ArrayUtils.isNotEmpty(columns)) {
            for (Column column : columns) {
                params.add(column.getValue());
            }
        }

        String sql = getDialect().forFindByColumns(tableName(), "*", columns, orderBy, count);
        return params.isEmpty() ? find(sql) : find(sql, params.toArray());
    }


    /**
     * 分页查询数据
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<M> paginate(int pageNumber, int pageSize, String orderBy) {
        return paginateByColumns(pageNumber, pageSize, null, orderBy);
    }


    /**
     * 根据某列信息，分页查询数据
     *
     * @param pageNumber
     * @param pageSize
     * @param column
     * @return
     */
    public Page<M> paginateByColumn(int pageNumber, int pageSize, Column column) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(column).getList(), null);
    }


    /**
     * 根据某列信息，分页查询数据
     *
     * @param pageNumber
     * @param pageSize
     * @param column
     * @return
     */
    public Page<M> paginateByColumn(int pageNumber, int pageSize, Column column, String orderBy) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(column).getList(), orderBy);
    }


    /**
     * 根据列信息，分页查询数据
     *
     * @param pageNumber
     * @param pageSize
     * @param columns
     * @return
     */
    public Page<M> paginateByColumns(int pageNumber, int pageSize, List<Column> columns) {
        return paginateByColumns(pageNumber, pageSize, columns, null);
    }


    /**
     * 根据列信息，分页查询数据
     *
     * @param pageNumber
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<M> paginateByColumns(int pageNumber, int pageSize, List<Column> columns, String orderBy) {
        String selectPartSql = getDialect().forPaginateSelect("*");
        String fromPartSql = getDialect().forPaginateFrom(tableName(), columns, orderBy);

        LinkedList<Object> params = new LinkedList<Object>();

        if (ArrayUtils.isNotEmpty(columns)) {
            for (Column column : columns) {
                params.add(column.getValue());
            }
        }
        return params.isEmpty() ? paginate(pageNumber, pageSize, selectPartSql, fromPartSql)
                : paginate(pageNumber, pageSize, selectPartSql, fromPartSql, params.toArray());
    }


    public String cacheName() {
        return tableName();
    }


    private transient Table table;

    public String tableName() {
        if (table == null) {
            table = TableMapping.me().getTable(getUsefulClass());
            if (table == null) {
                throw new JbootException(String.format("table for class[%s] is null! \n maybe cannot connection to database，please check your propertie files.", getUsefulClass()));
            }
        }
        return table.getName();
    }

    private transient String primaryKey;

    protected String getPrimaryKey() {
        if (primaryKey != null) {
            return primaryKey;
        }
        String[] primaryKeys = getPrimaryKeys();
        if (null != primaryKeys && primaryKeys.length == 1) {
            primaryKey = primaryKeys[0];
        }

        JbootAssert.assertTrue(primaryKey != null, String.format("get PrimaryKey is error in[%s]", getClass()));
        return primaryKey;
    }

    private transient Class<?> primaryType;

    public Class<?> getPrimaryType() {
        if (primaryType == null) {
            primaryType = TableMapping.me().getTable(getUsefulClass()).getColumnType(getPrimaryKey());
        }
        return primaryType;
    }

    protected String[] getPrimaryKeys() {
        Table t = TableMapping.me().getTable(getUsefulClass());
        if (t == null) {
            throw new RuntimeException("can't get table of " + getUsefulClass() + " , maybe jboot install incorrect");
        }
        return t.getPrimaryKey();
    }

    protected boolean hasColumn(String columnLabel) {
        return TableMapping.me().getTable(getUsefulClass()).hasColumnLabel(columnLabel);
    }

    // -----------------------------Override----------------------------
    @Override
    public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        return super.paginate(pageNumber, pageSize, select, sqlExceptSelect);
    }

    @Override
    public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
        return super.paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
    }

    @Override
    public Page<M> paginate(int pageNumber, int pageSize, boolean isGroupBySql, String select, String sqlExceptSelect,
                            Object... paras) {
        return super.paginate(pageNumber, pageSize, isGroupBySql, select, sqlExceptSelect, paras);
    }

    @Override
    public List<M> find(String sql, Object... paras) {
        debugPrintParas(paras);
        return super.find(sql, paras);
    }

    @Override
    public M findFirst(String sql, Object... paras) {
        debugPrintParas(paras);
        return super.findFirst(sql, paras);
    }

    @Override
    public List<M> findByCache(String cacheName, Object key, String sql, Object... paras) {
        return super.findByCache(cacheName, key, sql, paras);
    }


    @Override
    public M findFirstByCache(String cacheName, Object key, String sql, Object... paras) {
        return super.findFirstByCache(cacheName, key, sql, paras);
    }

    @Override
    public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
                                   String sqlExceptSelect, Object... paras) {
        return super.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect, paras);
    }

    @Override
    public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, boolean isGroupBySql,
                                   String select, String sqlExceptSelect, Object... paras) {
        return super.paginateByCache(cacheName, key, pageNumber, pageSize, isGroupBySql, select,
                sqlExceptSelect, paras);
    }

    @Override
    public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
                                   String sqlExceptSelect) {
        return super.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect);
    }

    private void debugPrintParas(Object... objects) {
        if (JFinal.me().getConstants().getDevMode()) {
            System.out.println("\r\n---------------Paras: " + Arrays.toString(objects) + "----------------");
        }
    }

    public Map<String, Object> _getAttrsAsMap() {
        return _getAttrs();
    }

    /**
     * 通过调用 keepCopier ，才能把从modelCopier复制过来的数据保存到数据库
     */
    public void keepCopier() {
        Table table = TableMapping.me().getTable(getUsefulClass());
        if (table == null) {
            throw new RuntimeException("can't get table of " + getUsefulClass() + " , maybe jboot install incorrect");
        }
        Map<String, Class<?>> map = table.getColumnTypeMap();
        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            Object o = get(entry.getKey());
            if (o != null) {
                set(entry.getKey(), o);
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof JbootModel)) {
            return false;
        }

        Object id = ((JbootModel) o).get(getPrimaryKey());
        if (id == null) {
            return false;
        }

        return id.equals(get(getPrimaryKey()));
    }
}
