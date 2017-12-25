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

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.*;
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

    public static final String AUTO_COPY_MODEL = "_auto_copy_model_";

    private static final String COLUMN_CREATED = "created";
    private static final String COLUMN_MODIFIED = "modified";

    /**
     * 是否启用自动缓存
     */
    private transient boolean cacheEnable = true;
    private transient int cacheTime = 60 * 60 * 24; // 1day


    /**
     * 添加数据到缓存
     *
     * @param key
     * @param value
     */
    public void putCache(Object key, Object value) {
        Jboot.me().getCache().put(getTableName(), key, value, cacheTime);
    }

    /**
     * 获取缓存中的数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getCache(Object key) {
        return Jboot.me().getCache().get(getTableName(), key);
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
        return Jboot.me().getCache().get(getTableName(), key, dataloader, cacheTime);
    }

    /**
     * 移除缓存数据
     *
     * @param key
     */
    public void removeCache(Object key) {
        if (key == null) return;
        Jboot.me().getCache().remove(getTableName(), key);
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
            m.put(_getAttrs());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * 在 RPC 传输的时候，通过 Controller 传入到Service
     * 不同的序列化方案 可能导致 getModifyFlag 并未设置，可能造成无法保存到数据库
     * 因此需要 通过这个方法 拷贝数据库对于字段，然后再进行更新或保存
     *
     * @return
     */
    public M copyModel() {
        M m = null;
        try {
            m = (M) getUsefulClass().newInstance();
            Table table = TableMapping.me().getTable(getUsefulClass());
            if (table == null) {
                throw new JbootException("can't get table of " + getUsefulClass() + " , maybe config incorrect");
            }
            Set<String> attrKeys = table.getColumnTypeMap().keySet();
            for (String attrKey : attrKeys) {
                Object o = this.get(attrKey);
                if (o != null) {
                    m.set(attrKey, o);
                }
            }
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

        proxy = copy().use("proxy").cacheEnable(this.cacheEnable).cacheTime(cacheTime);

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

        proxy = copy().use("slave").cacheEnable(this.cacheEnable).cacheTime(cacheTime);

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

        proxy = copy().use("master").cacheEnable(this.cacheEnable).cacheTime(cacheTime);

        if (proxy._getConfig() == null) {
            proxy.use(null);
        }

        set("__master__", proxy);
        return proxy;
    }


    /**
     * 是否启用自动缓存
     *
     * @param enable
     * @return
     */
    public M cacheEnable(boolean enable) {
        this.cacheEnable = enable;
        return (M) this;
    }

    public boolean cacheEnable() {
        return cacheEnable;
    }

    /**
     * 设置默认的缓存时间
     *
     * @param time 缓存时间，单位：秒
     * @return
     */
    public M cacheTime(int time) {
        this.cacheTime = time;
        return (M) this;
    }

    public int cacheTime() {
        return cacheTime;
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

        Boolean autoCopyModel = get(AUTO_COPY_MODEL);
        boolean saveSuccess = (autoCopyModel != null && autoCopyModel) ? copyModel().saveNormal() : saveNormal();
        if (saveSuccess) {
            Jboot.sendEvent(addAction(), this);
        }
        return saveSuccess;
    }


    boolean saveNormal() {
        return super.save();
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
            if (cacheEnable) {
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
        return model == null ? true : model.delete();
    }


    /**
     * 更新
     *
     * @return
     */
    @Override
    public boolean update() {
        if (hasColumn(COLUMN_MODIFIED)) {
            set(COLUMN_MODIFIED, new Date());
        }

        Boolean autoCopyModel = get(AUTO_COPY_MODEL);
        boolean updateSuccess = (autoCopyModel != null && autoCopyModel) ? copyModel().updateNormal() : updateNormal();
        if (updateSuccess) {
            Object id = get(getPrimaryKey());
            if (cacheEnable) {
                removeCache(id);
            }
            Jboot.sendEvent(updateAction(), findById(id));
        }
        return updateSuccess;
    }

    boolean updateNormal() {
        return super.update();
    }

    public String addAction() {
        return getTableName() + ":add";
    }

    public String deleteAction() {
        return getTableName() + ":delete";
    }

    public String updateAction() {
        return getTableName() + ":update";
    }

    /**
     * 根据ID查找model
     *
     * @param idValue the id value of the model
     * @return
     */
    @Override
    public M findById(final Object idValue) {
        return cacheEnable ? getCache(idValue, new IDataLoader() {
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
        String sql = getDialect().forFindByColumns(getTableName(), "*", Columns.create(column, value).getList(), null, 1);
        return findFirst(sql, value);
    }

    /**
     * 根据 列和值 查询1条数据
     *
     * @param column
     * @return
     */
    public M findFirstByColumn(Column column) {
        String sql = getDialect().forFindByColumns(getTableName(), "*", Columns.create(column).getList(), null, 1);
        return findFirst(sql, column.getValue());
    }

    /**
     * 根据 多列和值，查询1条数据
     *
     * @param columns
     * @return
     */
    public M findFirstByColumns(Columns columns) {
        String sql = getDialect().forFindByColumns(getTableName(), "*", columns.getList(), null, 1);
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
        String sql = getDialect().forFindByColumns(getTableName(), "*", null, null, null);
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

        String sql = getDialect().forFindByColumns(getTableName(), "*", columns, orderBy, count);
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
        String fromPartSql = getDialect().forPaginateFrom(getTableName(), columns, orderBy);

        LinkedList<Object> params = new LinkedList<Object>();

        if (ArrayUtils.isNotEmpty(columns)) {
            for (Column column : columns) {
                params.add(column.getValue());
            }
        }
        return params.isEmpty() ? paginate(pageNumber, pageSize, selectPartSql, fromPartSql)
                : paginate(pageNumber, pageSize, selectPartSql, fromPartSql, params.toArray());
    }


    private transient Table table;

    @JSONField(serialize = false)
    protected String getTableName() {
        if (table == null) {
            table = TableMapping.me().getTable(getUsefulClass());
            if (table == null) {
                throw new JbootException(String.format("table for class[%s] is null! \n maybe cannot connection to database，please check your propertie files.", getUsefulClass()));
            }
        }
        return table.getName();
    }


    private transient String primaryKey;

    @JSONField(serialize = false)
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


    @JSONField(serialize = false)
    protected Class<?> getPrimaryType() {
        if (primaryType == null) {
            primaryType = TableMapping.me().getTable(getUsefulClass()).getColumnType(getPrimaryKey());
        }
        return primaryType;
    }


    @JSONField(serialize = false)
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
