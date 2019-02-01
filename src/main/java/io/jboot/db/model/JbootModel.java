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
package io.jboot.db.model;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.*;
import io.jboot.db.dialect.IJbootModelDialect;
import io.jboot.exception.JbootException;
import io.jboot.utils.StrUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;


@SuppressWarnings("serial")
public class JbootModel<M extends JbootModel<M>> extends Model<M> {

    public static final String AUTO_COPY_MODEL = "_auto_copy_model_";

    private static JbootModelConfig config = JbootModelConfig.getConfig();
    private static String column_created = config.getColumnCreated();
    private static String column_modified = config.getColumnModified();
    private static boolean idCacheEnable = config.isIdCacheEnable();


    /**
     * copy new model with all attrs
     *
     * @return
     */
    public M copy() {
        M m = null;
        try {
            m = (M) _getUsefulClass().newInstance();
            m.put(_getAttrs());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * copy new model with db attrs and fill modifyFlag
     *
     * @return
     */
    public M copyModel() {
        M m = null;
        try {
            m = (M) _getUsefulClass().newInstance();
            Table table = _getTable(true);
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
     * 修复 jfinal use 可能造成的线程安全问题
     *
     * @param configName
     * @return
     */
    @Override
    public M use(String configName) {
        M m = this.get("__ds__" + configName);
        if (m == null) {
            synchronized (configName) {
                m = this.get("__ds__" + configName);
                if (m != null) return m;
                m = this.copy().superUse(configName);
                this.put("__ds__" + configName, m);
            }
        }
        return m;
    }


    M superUse(String configName) {
        return super.use(configName);
    }


    public boolean saveOrUpdate() {
        if (null == _getIdValue()) {
            return this.save();
        }
        return this.update();
    }


    @Override
    public boolean save() {
        if (_hasColumn(column_created) && get(column_created) == null) {
            set(column_created, new Date());
        }

        boolean needInitPrimaryKey = (String.class == _getPrimaryType() && null == get(_getPrimaryKey()));

        if (needInitPrimaryKey) {
            set(_getPrimaryKey(), generatePrimaryValue());
        }

        boolean saveSuccess = false;

        Boolean autoCopyModel = get(AUTO_COPY_MODEL);
        if (autoCopyModel != null && autoCopyModel == true) {
            M copyModel = copyModel();
            saveSuccess = copyModel.superSave();

            if (saveSuccess && !needInitPrimaryKey) {
                this.set(_getPrimaryKey(), copyModel.get(_getPrimaryKey()));
            }
        } else {
            saveSuccess = this.superSave();
        }

        return saveSuccess;
    }


    protected boolean superSave() {
        return super.save();
    }


    protected String generatePrimaryValue() {
        return StrUtil.uuid();
    }

    @Override
    public M findById(Object idValue) {
        if (idValue == null) {
            throw new IllegalArgumentException("idValue can not be null");
        }
        return idCacheEnable ? loadByCache(idValue) : super.findByIds(idValue);
    }

    @Override
    public M findByIds(Object... idValues) {
        if (idValues == null || idValues.length != _getPrimaryKeys().length) {
            throw new IllegalArgumentException("primary key nubmer must equals id value number and can not be null");
        }
        return idCacheEnable ? loadByCache(idValues) : super.findByIds(idValues);
    }

    protected M loadByCache(Object... idValues) {
        return config.getCache().get(_getTableName()
                , buildCacheKey(idValues)
                , () -> JbootModel.super.findByIds(idValues)
                , config.getIdCacheTime());
    }


    @Override
    public boolean delete() {
        boolean success = super.delete();
        if (success && idCacheEnable) {
            deleteIdCache();
        }
        return success;
    }

    @Override
    public boolean deleteById(Object idValue) {
        boolean success = super.deleteById(idValue);
        if (success && idCacheEnable) {
            deleteIdCacheById(idValue);
        }
        return success;
    }

    @Override
    public boolean deleteByIds(Object... idValues) {
        boolean success = super.deleteByIds(idValues);
        if (success && idCacheEnable) {
            deleteIdCacheById(idValues);
        }
        return success;
    }


    @Override
    public boolean update() {
        if (_hasColumn(column_modified)) {
            set(column_modified, new Date());
        }

        boolean success = isAutoCopyModel() ? copyModel().superUpdate() : this.superUpdate();

        if (success && idCacheEnable) {
            deleteIdCache();
        }

        return success;
    }


    private boolean isAutoCopyModel() {
        Boolean autoCopyModel = get(AUTO_COPY_MODEL);
        return autoCopyModel != null && autoCopyModel == true;
    }


    protected boolean superUpdate() {
        return super.update();
    }

    public void deleteIdCache() {
        if (_getPrimaryKeys().length == 1) {
            Object idValue = get(_getPrimaryKey());
            deleteIdCacheById(idValue);
        } else {
            Object[] idvalues = new Object[_getPrimaryKeys().length];
            for (int i = 0; i < idvalues.length; i++) {
                idvalues[i] = get(_getPrimaryKeys()[i]);
            }
            deleteIdCacheById(idvalues);
        }
    }

    public void deleteIdCacheById(Object... idvalues) {
        config.getCache().remove(_getTableName(), buildCacheKey(idvalues));
    }


    private static String buildCacheKey(Object... idValues) {
        if (idValues == null || idValues.length == 0) {
            return null;
        }

        if (idValues.length == 1) {
            return idValues[0].toString();
        }

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < idValues.length; i++) {
            key.append(idValues[i]);
            if (i < idValues.length - 1) {
                key.append(":");
            }
        }
        return key.toString();
    }

    protected IJbootModelDialect _getDialect() {
        Config config = _getConfig();
        if (config == null) {
            throw new JbootException(
                    String.format("class %s can not mapping to database table, " +
                                    "maybe application cannot connect to database , " +
                                    "please check jboot.properties " +
                                    "or config @Table(datasource=xxx) correct if you use multi datasource."
                            , _getUsefulClass().getName()));

        }
        return (IJbootModelDialect) config.getDialect();
    }


    public M findFirstByColumn(String column, Object value) {
        return findFirstByColumn(Column.create(column, value));
    }


    public M findFirstByColumn(String column, Object value, String orderBy) {
        return findFirstByColumn(Column.create(column, value), orderBy);
    }

    public M findFirstByColumn(Column column) {
        return findFirstByColumns(Columns.create(column));
    }


    public M findFirstByColumn(Column column, String orderBy) {
        return findFirstByColumns(Columns.create(column), orderBy);
    }


    public M findFirstByColumns(Columns columns) {
        return findFirstByColumns(columns, null);
    }


    public M findFirstByColumns(Columns columns, String orderby) {
        String sql = _getDialect().forFindByColumns(_getTableName(), "*", columns.getList(), orderby, 1);
        return columns.isEmpty() ? findFirst(sql) : findFirst(sql, columns.getValueArray());
    }


    public List<M> findAll() {
        String sql = _getDialect().forFindByColumns(_getTableName(), "*", null, null, null);
        return find(sql);
    }


    public List<M> findListByColumn(String column, Object value) {
        return findListByColumn(Column.create(column, value), null, null);
    }

    public List<M> findListByColumn(Column column) {
        return findListByColumn(column, null, null);
    }


    public List<M> findListByColumn(String column, Object value, Integer count) {
        return findListByColumn(Column.create(column, value), null, count);
    }

    public List<M> findListByColumn(Column column, Integer count) {
        return findListByColumn(column, null, count);
    }


    public List<M> findListByColumn(String column, Object value, String orderBy) {
        return findListByColumn(Column.create(column, value), orderBy, null);
    }


    public List<M> findListByColumn(Column column, String orderby) {
        return findListByColumn(column, orderby, null);
    }

    public List<M> findListByColumn(String column, Object value, String orderBy, Integer count) {
        return findListByColumn(Column.create(column, value), orderBy, count);
    }

    public List<M> findListByColumn(Column column, String orderBy, Integer count) {
        return findListByColumns(Columns.create(column), orderBy, count);
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

    public List<M> findListByColumns(List<Column> columns, String orderBy, Integer count) {
        return findListByColumns(Columns.create(columns), orderBy, count);
    }

    public List<M> findListByColumns(Columns columns) {
        return findListByColumns(columns, null, null);
    }

    public List<M> findListByColumns(Columns columns, String orderBy) {
        return findListByColumns(columns, orderBy, null);
    }

    public List<M> findListByColumns(Columns columns, Integer count) {
        return findListByColumns(columns, null, count);
    }


    public List<M> findListByColumns(Columns columns, String orderBy, Integer count) {
        String sql = _getDialect().forFindByColumns(_getTableName(), "*", columns.getList(), orderBy, count);
        return columns.isEmpty() ? find(sql) : find(sql, columns.getValueArray());
    }


    public Page<M> paginate(int pageNumber, int pageSize) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(), null);
    }


    public Page<M> paginate(int pageNumber, int pageSize, String orderBy) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(), orderBy);
    }


    public Page<M> paginateByColumn(int pageNumber, int pageSize, Column column) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(column), null);
    }


    public Page<M> paginateByColumn(int pageNumber, int pageSize, Column column, String orderBy) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(column), orderBy);
    }


    public Page<M> paginateByColumns(int pageNumber, int pageSize, Columns columns) {
        return paginateByColumns(pageNumber, pageSize, columns, null);
    }


    public Page<M> paginateByColumns(int pageNumber, int pageSize, List<Column> columns) {
        return paginateByColumns(pageNumber, pageSize, columns, null);
    }


    public Page<M> paginateByColumns(int pageNumber, int pageSize, List<Column> columns, String orderBy) {
        return paginateByColumns(pageNumber, pageSize, Columns.create(columns), orderBy);
    }


    public Page<M> paginateByColumns(int pageNumber, int pageSize, Columns columns, String orderBy) {
        String selectPartSql = _getDialect().forPaginateSelect("*");
        String fromPartSql = _getDialect().forPaginateFrom(_getTableName(), columns.getList(), orderBy);

        return columns.isEmpty()
                ? paginate(pageNumber, pageSize, selectPartSql, fromPartSql)
                : paginate(pageNumber, pageSize, selectPartSql, fromPartSql, columns.getValueArray());
    }

    public <T> T _getIdValue() {
        return get(_getPrimaryKey());
    }


    public String _getTableName() {
        return _getTable(true).getName();
    }

    public Table _getTable() {
        return _getTable(false);
    }

    private transient Table table;

    public Table _getTable(boolean validateNull) {
        if (table == null) {
            table = super._getTable();
            if (table == null && validateNull) {
                throw new JbootException(
                        String.format("class %s can not mapping to database table, " +
                                        "maybe application cannot connect to database , " +
                                        "please check jboot.properties " +
                                        "or config @Table(datasource=xxx) correct if you use multi datasource."
                                , _getUsefulClass().getName()));
            }
        }
        return table;
    }


    public String _getPrimaryKey() {
        return _getPrimaryKeys()[0];
    }

    private transient String[] primaryKeys;

    public String[] _getPrimaryKeys() {
        if (primaryKeys != null) {
            return primaryKeys;
        }
        primaryKeys = _getTable(true).getPrimaryKey();

        if (primaryKeys == null) {
            throw new JbootException(String.format("get PrimaryKey is error in[%s]", getClass()));
        }
        return primaryKeys;
    }


    private transient Class<?> primaryType;

    protected Class<?> _getPrimaryType() {
        if (primaryType == null) {
            primaryType = _getTable(true).getColumnType(_getPrimaryKey());
        }
        return primaryType;
    }


    protected boolean _hasColumn(String columnLabel) {
        return _getTable(true).hasColumnLabel(columnLabel);
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

        if (o == null || !(o instanceof JbootModel)) {
            return false;
        }

        //可能model在rpc的Controller层，没有映射到数据库
        if (_getTable(false) == null) {
            return this == o;
        }

        Object id = ((JbootModel) o)._getIdValue();
        return id != null && id.equals(_getIdValue());
    }


    public M preventXssAttack() {
        String[] attrNames = _getAttrNames();
        for (String attrName : attrNames) {
            Object value = get(attrName);
            if (value == null || !(value instanceof String)) {
                continue;
            }

            set(attrName, StrUtil.escapeHtml((String) value));
        }
        return (M) this;
    }


    public M preventXssAttack(String... ignoreAttrs) {
        String[] attrNames = _getAttrNames();
        for (String attrName : attrNames) {
            Object value = get(attrName);
            if (value == null || !(value instanceof String)) {
                continue;
            }

            boolean isContinue = false;
            for (String ignoreAttr : ignoreAttrs) {
                if (attrName.equals(ignoreAttr)) {
                    isContinue = true;
                    break;
                }
            }

            if (isContinue) continue;
            set(attrName, StrUtil.escapeHtml((String) value));
        }

        return (M) this;
    }

}
