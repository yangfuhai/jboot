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

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.db.JbootDb;
import io.jboot.db.SqlDebugger;
import io.jboot.db.dialect.JbootDialect;
import io.jboot.exception.JbootException;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * @author michael yang
 */
public class JbootModel<M extends JbootModel<M>> extends Model<M> {

    private static final Log LOG = Log.getLog(JbootModel.class);
    private static final String DATASOURCE_CACHE_PREFIX = "__ds__";

    private static JbootModelConfig config = JbootModelConfig.getConfig();
    private static String column_created = config.getColumnCreated();
    private static String column_modified = config.getColumnModified();
    private static boolean idCacheEnable = config.isIdCacheEnable();

    protected List<Join> joins = null;
    String datasourceName = null;
    String alias = null;
    String loadColumns = null;
    boolean isCopyModel = false;


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

    /**
     * set table alias in sql
     *
     * @param alias
     * @return
     */
    public M alias(String alias) {
        if (StrUtil.isBlank(alias)) {
            throw new IllegalArgumentException("alias must not be null or empty.");
        }
        M model = getOrCopyDao();
        model.alias = alias;
        return model;
    }


    protected Joiner<M> joining(String type, String table, boolean condition) {
        M model = getOrCopyDao();
        if (model.joins == null) {
            model.joins = new LinkedList<>();
        }
        Join join = new Join(type, table, condition);
        model.joins.add(join);
        return new Joiner<>(model, join);
    }


    /**
     * set load columns in sql
     *
     * @param loadColumns
     * @return
     */
    public M loadColumns(String loadColumns) {
        if (StrUtil.isBlank(loadColumns)) {
            throw new IllegalArgumentException("loadColumns must not be null or empty.");
        }
        M model = getOrCopyDao();
        model.loadColumns = loadColumns;
        return model;
    }


    public M distinct(String columnName) {
        if (StrUtil.isBlank(columnName)) {
            throw new IllegalArgumentException("columnName must not be null or empty.");
        }
        M dao = getOrCopyDao();
        JbootModelExts.setDistinctColumn(dao, columnName);
        return dao;
    }


    private M getOrCopyDao() {
        if (isCopyModel) {
            return (M) this;
        } else {
            M dao = copy()._setConfigName(datasourceName);
            dao.isCopyModel = true;
            return dao;
        }
    }

    @Override
    public M dao() {
        put("__is_dao", true);
        return (M) this;
    }


    private boolean isDaoModel() {
        Boolean flag = getBoolean("__is_dao");
        return flag != null && flag;
    }

    /**
     * copy model with attrs or false
     *
     * @return
     */
    public M copy() {
        M m = null;
        try {
            m = (M) _getUsefulClass().newInstance();
            m.put(_getAttrs());

            for (String attr : _getModifyFlag()) {
                m._getModifyFlag().add(attr);
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
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
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return m;
    }


    /**
     * 修复 JFinal use 造成的线程安全问题
     *
     * @param configName
     * @return
     */
    @Override
    public M use(String configName) {
        return use(configName, true);
    }


    /**
     * 优先使用哪个数据源进行查询
     *
     * @param configNames
     * @return
     */
    public M useFirst(String... configNames) {
        if (configNames == null || configNames.length == 0) {
            throw new IllegalArgumentException("configNames must not be null or empty.");
        }

        for (String name : configNames) {
            M newDao = use(name, false);
            if (newDao != null) {
                return newDao;
            }
        }
        return (M) this;
    }


    private M use(String configName, boolean validateDatasourceExist) {

        //非 service 的 dao，例如 new User().user('ds').save()/upate()
        if (!isDaoModel()) {
            _setConfigName(configName);
            return validDatasourceExist((M) this, validateDatasourceExist, configName);
        }

        //定义在 service 中的 DAO
        M newDao = JbootModelExts.getDatasourceDAO(this, DATASOURCE_CACHE_PREFIX + configName);
        if (newDao == null) {
            newDao = this.copy()._setConfigName(configName);
            newDao = validDatasourceExist(newDao, validateDatasourceExist, configName);
            if (newDao != null) {
                JbootModelExts.setDatasourceDAO(this, DATASOURCE_CACHE_PREFIX + configName, newDao);
            }
        }
        return newDao;
    }


    private M validDatasourceExist(M model, boolean valid, String configName) {
        if (model._getConfig() == null) {
            if (valid) {
                throw new JbootIllegalConfigException("The datasource \"" + configName + "\" not config well, please config it in jboot.properties.");
            } else {
                return null;
            }
        }
        return model;
    }


    M _setConfigName(String configName) {
        this.datasourceName = configName;
        return (M) this;
    }


    @Override
    protected Config _getConfig() {
        if (datasourceName != null) {
            return DbKit.getConfig(datasourceName);
        }

        String currentConfigName = JbootDb.getCurrentConfigName();
        if (StrUtil.isNotBlank(currentConfigName)) {
            Config config = DbKit.getConfig(currentConfigName);
            if (config == null) {
                LogKit.error("Can not use the datasource: {}, user default to replace.", currentConfigName);
            } else {
                return config;
            }
        }

        return DbKit.getConfig(_getUsefulClass());
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
        List<Object> paras = new ArrayList<>();

        Dialect dialect = _getConfig().getDialect();
        dialect.forModelSave(table, _getAttrs(), sql, paras);

        try {
            return SqlDebugger.run(() -> {
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
                } finally {
                    config.close(pst, conn);
                }
            }, config, sql.toString(), paras.toArray());
        } catch (SQLException e) {
            throw new ActiveRecordException(e);
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

    /**
     * 直接查询数据库，不走缓存
     *
     * @param idValue
     * @return
     */
    public M findByIdWithoutCache(Object idValue) {
        if (idValue == null) {
            return null;
        }
        return super.findById(idValue);
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

    /**
     * 直接查询数据库，不走缓存
     *
     * @param idValues
     * @return
     */
    public M findByIdsWithoutCache(Object... idValues) {
        if (idValues == null) {
            return null;
        }
        if (idValues.length != _getPrimaryKeys().length) {
            throw new IllegalArgumentException("idValues.length != _getPrimaryKeys().length");
        }
        return super.findByIds(idValues);
    }


    protected M loadByCache(Object... idValues) {
        try {
            M m = config.getIdCache().get(buildIdCacheName(_getTableName())
                    , buildIdCacheKey(idValues)
                    , () -> JbootModel.super.findByIds(idValues)
                    , config.getIdCacheTime());
            return m != null && config.isIdCacheByCopyEnable() ? m.copy() : m;
        } catch (Exception ex) {
            LOG.error("Jboot load model [" + ClassUtil.getUsefulClass(getClass()) + "] by cache is error, safe deleted it in cache.", ex);
            safeDeleteCache(idValues);
        }

        return JbootModel.super.findByIds(idValues);
    }


    protected void safeDeleteCache(Object... idValues) {
        try {
            config.getIdCache().remove(buildIdCacheName(_getTableName())
                    , buildIdCacheKey(idValues));
        } catch (Exception ex) {
            LOG.error("Remove cache is error by name [" + buildIdCacheName(_getTableName()) + "] and key [" + buildIdCacheKey(idValues) + "]", ex);
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
        if (column == null || !column.checkAvailable()) {
            throw new IllegalArgumentException("Column or value must not be null.");
        }
        return deleteByColumns(Columns.create(column));
    }


    public boolean deleteByColumns(Columns columns) {
        processColumns(columns, "delete");

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("Columns must not be null or empty.");
        }
        String sql = _getDialect().forDeleteByColumns(alias, joins, _getTableName(), columns.getList());
        return Db.use(_getConfig().getName()).update(sql, Util.getValueArray(columns.getList())) >= 1;
    }


    public boolean deleteAll() {
        Columns columns = Columns.create();

        //通过 processColumns 可以重构 deleteAll 的行为
        processColumns(columns, "deleteAll");

        String sql = _getDialect().forDeleteByColumns(alias, joins, _getTableName(), columns.getList());
        return Db.use(_getConfig().getName()).update(sql, Util.getValueArray(columns.getList())) >= 1;
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


    protected String buildIdCacheName(String orginal) {
        return orginal;
    }

    protected String buildIdCacheKey(Object... idValues) {
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
            return throwCannotMappingException();
        }
        return (JbootDialect) config.getDialect();
    }


    private JbootDialect throwCannotMappingException() {
        io.jboot.db.annotation.Table annotation = this.getClass().getAnnotation(io.jboot.db.annotation.Table.class);
        if (annotation != null && StrUtil.isNotBlank(annotation.datasource())) {
            throw new JbootException(
                    String.format("Model \"%s\" can not mapping to datasource: " + annotation.datasource()
                            , _getUsefulClass().getName()));
        } else {
            throw new JbootException(
                    String.format("Model \"%s\" can not mapping to database table, maybe application cannot connect to database. "
                            , _getUsefulClass().getName()));
        }
    }


    public M findFirstByColumn(String column, Object value) {
        return findFirstByColumn(Column.create(column, value));
    }


    public M findFirstByColumn(String column, Object value, String orderBy) {
        return findFirstByColumn(Column.create(column, value), orderBy);
    }

    public M findFirstByColumn(Column column) {
        if (column == null || !column.checkAvailable()) {
//            throw new IllegalArgumentException("Column or value must not be null.");
            return null;
        }
        return findFirstByColumns(Columns.create(column));
    }


    public M findFirstByColumn(Column column, String orderBy) {
        if (column == null || !column.checkAvailable()) {
//            throw new IllegalArgumentException("Column or value must not be null.");
            return null;
        }
        return findFirstByColumns(Columns.create(column), orderBy);
    }


    public M findFirstByColumns(Columns columns) {
        return findFirstByColumns(columns, null);
    }


    public M findFirstByColumns(Columns columns, String orderby) {
        return findFirstByColumns(columns, orderby, null);
    }

    public M findFirstByColumns(Columns columns, String orderby, String loadColumns) {
        processColumns(columns, "findFirst");
        if (StrUtil.isBlank(loadColumns) && this.loadColumns != null) {
            loadColumns = this.loadColumns;
        }
        if (StrUtil.isBlank(loadColumns)) {
            loadColumns = "*";
        }
        String sql = _getDialect().forFindByColumns(alias, joins, _getTableName(), loadColumns, columns.getList(), orderby, 1);
        return columns.isEmpty() ? findFirst(sql) : findFirst(sql, columns.getValueArray());
    }


    public List<M> findListByIds(Object... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        List<M> list = new ArrayList<>();
        for (Object id : ids) {
            if (id.getClass() == int[].class) {
                findListByIds(list, (int[]) id);
            } else if (id.getClass() == long[].class) {
                findListByIds(list, (long[]) id);
            } else if (id.getClass() == short[].class) {
                findListByIds(list, (short[]) id);
            } else {
                M model = findById(id);
                if (model != null) {
                    list.add(model);
                }
            }
        }
        return list;
    }

    private void findListByIds(List<M> list, int[] ids) {
        for (int id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
    }

    private void findListByIds(List<M> list, long[] ids) {
        for (long id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
    }


    private void findListByIds(List<M> list, short[] ids) {
        for (short id : ids) {
            M model = findById(id);
            if (model != null) {
                list.add(model);
            }
        }
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
        if (column == null || !column.checkAvailable()) {
            return null;
        }
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
        return findListByColumns(columns, orderBy, count, null);
    }

    public List<M> findListByColumns(Columns columns, String orderBy, Integer count, String loadColumns) {
        processColumns(columns, "findList");
        loadColumns = getLoadColumns(loadColumns);
        String sql = _getDialect().forFindByColumns(alias, joins, _getTableName(), loadColumns, columns.getList(), orderBy, count);
        return columns.isEmpty() ? find(sql) : find(sql, columns.getValueArray());
    }


    //方便在某些场景下，对 columns 进行二次加工
    protected void processColumns(Columns columns, String action) {
    }

    @Override
    protected Class<? extends Model> _getUsefulClass() {
        Class c = getClass();
        // guice : Model$$EnhancerByGuice$$40471411
        // cglib : com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
        // return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
        // return c.getName().indexOf("$$EnhancerBy") == -1 ? c : c.getSuperclass();

        //不支持匿名类，匿名无法被创建
        return c.getName().indexOf("$") == -1 ? c : c.getSuperclass();
    }

    private String getLoadColumns(String loadColumns) {
        if (StrUtil.isBlank(loadColumns) && StrUtil.isNotBlank(this.loadColumns)) {
            loadColumns = this.loadColumns;
        }

        //使用 join 的情况下，需要判断 distinct
        if (hasAnyJoinEffective()) {
            String distinctColumn = JbootModelExts.getDistinctColumn(this);

            //用户配置了 distinct
            if (StrUtil.isNotBlank(distinctColumn)) {
                if (StrUtil.isBlank(loadColumns)) {
                    loadColumns = (StrUtil.isNotBlank(alias) ? alias : _getTableName()) + ".*";
                }

                //用户配置的 loadColumns 未包含 distinct 关键字
                if (!loadColumns.toLowerCase().contains("distinct ")) {
                    loadColumns = "DISTINCT " + distinctColumn + ", " + loadColumns;
                }
            }
        }

        if (StrUtil.isBlank(loadColumns)) {
            loadColumns = "*";
        }

        return loadColumns;
    }


    boolean hasAnyJoinEffective() {
        if (joins == null || joins.size() == 0) {
            return false;
        }

        for (Join join : joins) {
            if (join.isEffective()) {
                return true;
            }
        }

        return false;
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
        return paginateByColumns(pageNumber, pageSize, columns, orderBy, null);
    }

    public Page<M> paginateByColumns(int pageNumber, int pageSize, Columns columns, String orderBy, String loadColumns) {
        processColumns(columns, "paginate");

        loadColumns = getLoadColumns(loadColumns);


        String selectPartSql = _getDialect().forPaginateSelect(loadColumns);
        String fromPartSql = _getDialect().forPaginateFrom(alias, joins, _getTableName(), columns.getList(), orderBy);

//        return columns.isEmpty()
//                ? paginate(pageNumber, pageSize, selectPartSql, fromPartSql)
//                : paginate(pageNumber, pageSize, selectPartSql, fromPartSql, columns.getValueArray());

        Config config = _getConfig();
        Connection conn = null;
        try {
            conn = config.getConnection();
//            String totalRowSql = config.dialect.forPaginateTotalRow(select, sqlExceptSelect, this);
            String totalRowSqlExceptSelect = _getDialect().forPaginateFrom(alias, joins, _getTableName(), columns.getList(), null);
            String totalRowSql = config.getDialect().forPaginateTotalRow(selectPartSql, totalRowSqlExceptSelect, this);

            StringBuilder findSql = new StringBuilder();
            findSql.append(selectPartSql).append(' ').append(fromPartSql);

            return doPaginateByFullSql(config, conn, pageNumber, pageSize, null, totalRowSql, findSql, columns.getValueArray());
        } catch (Exception e) {
            throw new ActiveRecordException(e);
        } finally {
            config.close(conn);
        }
    }


    public long findCountByColumn(Column column) {
        return findCountByColumns(Columns.create(column));
    }


    public long findCountByColumns(Columns columns) {
        processColumns(columns, "findCount");

        String loadColumns = "*";

        //使用 distinct
        if (hasAnyJoinEffective()) {
            String distinctColumn = JbootModelExts.getDistinctColumn(this);
            if (StrUtil.isNotBlank(distinctColumn)) {
                loadColumns = "DISTINCT " + distinctColumn;
            }
        }


        String sql = _getDialect().forFindCountByColumns(alias, joins, _getTableName(), loadColumns, columns.getList());
        Long value = Db.use(_getConfig().getName()).queryLong(sql, Util.getValueArray(columns.getList()));
        return value == null ? 0 : value;
    }


    public <T> T _getIdValue() {
        return get(_getPrimaryKey());
    }


    public Object[] _getIdValues() {
        String[] pkeys = _getPrimaryKeys();
        Object[] values = new Object[pkeys.length];

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
                throwCannotMappingException();
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
    public boolean equals(Object o) {
        if (!(o instanceof JbootModel)) {
            return false;
        }

        //可能model在rpc的Controller层，没有映射到数据库
        if (_getTable(false) == null) {
            return this == o;
        }

        Object id = ((JbootModel<?>) o)._getIdValue();
        return id != null && id.equals(this._getIdValue());
    }


    @Override
    public int hashCode() {
        //可能model在rpc的Controller层，没有映射到数据库
        if (_getTable(false) == null) {
            return Objects.hash(_getAttrValues());
        }

        final Object[] idValues = _getIdValues();
        return idValues.length > 0 ? Objects.hash(idValues) : Objects.hash(_getAttrValues());
    }

    public M preventXssAttack() {
        String[] attrNames = _getAttrNames();
        for (String attrName : attrNames) {
            Object value = get(attrName);
            if (!(value instanceof String)) {
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
            if (!(value instanceof String)) {
                continue;
            }

            boolean isIgnoreAttr = false;
            for (String ignoreAttr : ignoreAttrs) {
                if (attrName.equals(ignoreAttr)) {
                    isIgnoreAttr = true;
                    break;
                }
            }

            if (!isIgnoreAttr) {
                set(attrName, StrUtil.escapeHtml((String) value));
            }
        }

        return (M) this;
    }


    /**
     * Override for print sql
     *
     * @param config
     * @param conn
     * @param sql
     * @param paras
     * @return
     * @throws Exception
     */
    @Override
    protected List<M> find(Config config, Connection conn, String sql, Object... paras) throws Exception {
        return SqlDebugger.run(() -> {
            try {
                return super.find(config, conn, sql, paras);
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                } else {
                    throw new SQLException(e);
                }
            }
        }, config, sql, paras);
    }

}
