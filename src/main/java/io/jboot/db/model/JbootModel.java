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

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.db.SqlDebugger;
import io.jboot.db.dialect.JbootDialect;
import io.jboot.exception.JbootException;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;


/**
 * @author michael yang
 */
@SuppressWarnings("serial")
public class JbootModel<M extends JbootModel<M>> extends Model<M> {

    private static final Log LOG = Log.getLog(JbootModel.class);
    private static final String DATASOURCE_CACHE_PREFIX = "__ds__";

    private static JbootModelConfig config = JbootModelConfig.getConfig();
    private static String column_created = config.getColumnCreated();
    private static String column_modified = config.getColumnModified();
    private static boolean idCacheEnable = config.isIdCacheEnable();

    protected List<Join> joins = null;

    public Joiner<M> leftJoin(String table) {
        return joining(Join.TYPE_LEFT, table, true);
    }

    public Joiner<M> leftJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_LEFT, table, condition);
    }

    public Joiner<M> rightJoin(String table) {
        return joining(Join.TYPE_RIGHT, table, true);
    }

    public Joiner<M> rightJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_RIGHT, table, condition);
    }

    public Joiner<M> innerJoin(String table) {
        return joining(Join.TYPE_INNER, table, true);
    }

    public Joiner<M> innerJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_INNER, table, condition);
    }

    public Joiner<M> fullJoin(String table) {
        return joining(Join.TYPE_FULL, table, true);
    }

    public Joiner<M> fullJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_FULL, table, condition);
    }


    protected Joiner<M> joining(String type, String table, boolean condition) {
        M model = joins == null ? copy() : (M) this;
        if (model.joins == null) {
            model.joins = new LinkedList<>();
        }
        Join join = new Join(type, table, condition);
        model.joins.add(join);
        return new Joiner<>(model, join);
    }


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
        M m = this.get(DATASOURCE_CACHE_PREFIX + configName);
        if (m == null) {
            synchronized (configName.intern()) {
                m = this.get(DATASOURCE_CACHE_PREFIX + configName);
                if (m == null) {
                    m = this.copy().superUse(configName);
                    this.put(DATASOURCE_CACHE_PREFIX + configName, m);
                }
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

//        boolean needInitPrimaryKey = (String.class == _getPrimaryType() && null == get(_getPrimaryKey()));
//
//        if (needInitPrimaryKey) {
//            set(_getPrimaryKey(), generatePrimaryValue());
//        }

        // 生成主键，只对单一主键的表生成，如果是多主键，不生成。
        String[] pkeys = _getPrimaryKeys();
        if (pkeys != null && pkeys.length == 1 && get(pkeys[0]) == null) {
            Object value = config.getPrimarykeyValueGenerator().genValue(this, _getPrimaryType());
            if (value != null) {
                set(pkeys[0], value);
            }
        }


        filter(FILTER_BY_SAVE);

        Config config = _getConfig();
        Table table = _getTable();

        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<Object>();

        Dialect dialect = _getConfig().getDialect();

        dialect.forModelSave(table, _getAttrs(), sql, paras);
        // if (paras.size() == 0)	return false;	// The sql "insert into tableName() values()" works fine, so delete this line

        // --------
        Connection conn = null;
        PreparedStatement pst = null;
        int result = 0;
        try {
            conn = config.getConnection();
            if (dialect.isOracle()) {
                pst = conn.prepareStatement(sql.toString(), table.getPrimaryKey());
            } else {
                pst = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            }
            dialect.fillStatement(pst, paras);
            result = pst.executeUpdate();
            dialect.getModelGeneratedKey(this, pst, table);
            _getModifyFlag().clear();
            return result >= 1;
        } catch (Exception e) {
            throw new ActiveRecordException(e);
        } finally {
            //add sqlDebugger print sql
            SqlDebugger.debug(config, sql.toString(), paras.toArray());
            config.close(pst, conn);
        }
    }


    @Override
    protected void filter(int filterBy) {
        config.getFilter().filter(this, filterBy);
    }

    @Override
    public M findById(Object idValue) {
        if (idValue == null) {
            return null;
        }
        return idCacheEnable ? loadByCache(idValue) : super.findById(idValue);
    }

    @Override
    public M findByIds(Object... idValues) {
        if (idValues == null) {
            return null;
        }
        if (idValues.length != _getPrimaryKeys().length) {
            throw new IllegalArgumentException("idValues.length != _getPrimaryKeys().length");
        }
        return idCacheEnable ? loadByCache(idValues) : super.findByIds(idValues);
    }

    protected M loadByCache(Object... idValues) {
        try {
            return config.getIdCache().get(_getTableName()
                    , buildCacheKey(idValues)
                    , () -> JbootModel.super.findByIds(idValues)
                    , config.getIdCacheTime());
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            safeDeleteCache(idValues);
        }

        return JbootModel.super.findByIds(idValues);
    }

    protected void safeDeleteCache(Object... idValues) {
        try {
            config.getIdCache().remove(_getTableName()
                    , buildCacheKey(idValues));
        } catch (Exception ex) {
            LOG.error("remove cache exception by name [" + _getTableName() + "] and key [" + buildCacheKey(idValues) + "]", ex);
        }
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


    public boolean deleteByColumn(Column column) {
        return deleteByColumns(Arrays.asList(column));
    }


    public boolean deleteByColumns(Columns columns) {
        return deleteByColumns(columns.getList());
    }


    public boolean deleteByColumns(List<Column> columns) {
        String sql = _getDialect().forDeleteByColumns(joins, _getTableName(), columns);
        return Db.use(_getConfig().getName()).update(sql, Util.getValueArray(columns)) >= 1;
    }


    public boolean batchDeleteByIds(Object... idValues) {
        if (idValues == null || idValues.length == 0) {
            return false;
        }
        boolean success = deleteByColumns(Columns.create().orEqs(_getPrimaryKey(), idValues));
        if (success && idCacheEnable) {
            for (Object id : idValues) {
                deleteIdCacheById(id);
            }
        }
        return success;
    }


    @Override
    public boolean update() {
        if (_hasColumn(column_modified)) {
            set(column_modified, new Date());
        }

        boolean success = super.update();

        if (success && idCacheEnable) {
            deleteIdCache();
        }

        return success;
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
        safeDeleteCache(idvalues);
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

    protected JbootDialect _getDialect() {
        Config config = _getConfig();
        if (config == null) {
            throw new JbootException(
                    String.format("class %s can not mapping to database table, maybe cannot connect to database. "
                            , _getUsefulClass().getName()));

        }
        return (JbootDialect) config.getDialect();
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
        return findFirstByColumns(columns, orderby, "*");
    }

    public M findFirstByColumns(Columns columns, String orderby, String loadColumns) {
        String sql = _getDialect().forFindByColumns(joins, _getTableName(), loadColumns, columns.getList(), orderby, 1);
        return columns.isEmpty() ? findFirst(sql) : findFirst(sql, columns.getValueArray());
    }


    public List<M> findListByIds(Object... ids) {
        return findListByColumns(Columns.create().in(_getPrimaryKey(), ids));
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
        return findListByColumns(columns, orderBy, count, "*");
    }

    public List<M> findListByColumns(Columns columns, String orderBy, Integer count, String loadColumns) {
        String sql = _getDialect().forFindByColumns(joins, _getTableName(), loadColumns, columns.getList(), orderBy, count);
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
        return paginateByColumns(pageNumber, pageSize, columns, orderBy, "*");
    }

    public Page<M> paginateByColumns(int pageNumber, int pageSize, Columns columns, String orderBy, String loadColumns) {
        String selectPartSql = _getDialect().forPaginateSelect(loadColumns);
        String fromPartSql = _getDialect().forPaginateFrom(joins, _getTableName(), columns.getList(), orderBy);

        return columns.isEmpty()
                ? paginate(pageNumber, pageSize, selectPartSql, fromPartSql)
                : paginate(pageNumber, pageSize, selectPartSql, fromPartSql, columns.getValueArray());
    }


    public long findCountByColumn(Column column) {
        return findCountByColumns(Columns.create(column));
    }


    public long findCountByColumns(Columns columns) {
        return findCountByColumns(columns.getList());
    }

    public long findCountByColumns(List<Column> columns) {
        String sql = _getDialect().forFindCountByColumns(joins, _getTableName(), columns);
        Long value = Db.use(_getConfig().getName()).queryLong(sql, Util.getValueArray(columns));
        return value == null ? 0 : value;
    }


    public <T> T _getIdValue() {
        return get(_getPrimaryKey());
    }

    public <T> T[] _getIdValues(Class<T> clazz) {
        String[] pkeys = _getPrimaryKeys();
        T[] values = (T[]) Array.newInstance(clazz, pkeys.length);

        int i = 0;
        for (String key : pkeys) {
            values[i++] = get(key);
        }
        return values;
    }


    public String _getTableName() {
        return _getTable(true).getName();
    }

    @Override
    public Table _getTable() {
        return _getTable(false);
    }

    private transient Table table;

    public Table _getTable(boolean validateMapping) {
        if (table == null) {
            table = super._getTable();
            if (table == null && validateMapping) {
                throw new JbootException(
                        String.format("class %s can not mapping to database table,maybe application cannot connect to database. "
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
            throw new JbootException(String.format("primaryKeys == null in [%s]", getClass()));
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


    @Override
    protected List<M> find(Config config, String sql, Object... paras) {
        SqlDebugger.debug(config, sql, paras);
        return super.find(config, sql, paras);
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

            boolean isIgnoreAttr = false;
            for (String ignoreAttr : ignoreAttrs) {
                if (attrName.equals(ignoreAttr)) {
                    isIgnoreAttr = true;
                    break;
                }
            }

            if (isIgnoreAttr) {
                continue;
            } else {
                set(attrName, StrUtil.escapeHtml((String) value));
            }
        }

        return (M) this;
    }

}
