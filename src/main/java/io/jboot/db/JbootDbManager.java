/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.Jboot;
import io.jboot.core.cache.JbootCache;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.db.dbpro.JbootDbProFactory;
import io.jboot.db.dialect.*;
import io.jboot.exception.JbootException;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.kits.ArrayKits;
import io.jboot.kits.ClassKits;
import io.jboot.kits.StringKits;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.*;


/**
 * 数据库 管理
 */
public class JbootDbManager {

    private static JbootDbManager manager;


    private List<ActiveRecordPlugin> activeRecordPlugins = new ArrayList<>();


    public static JbootDbManager me() {
        if (manager == null) {
            manager =new JbootDbManager();
        }
        return manager;
    }

    public JbootDbManager() {


        Map<String, DataSourceConfig> allDatasourceConfigs = DataSourceConfigManager.me().getDatasourceConfigs();

        // 包含了指定表配置的数据源
        Map<String, DataSourceConfig> hasTableDatasourceConfigs = new HashMap<>();


        Iterator<Map.Entry<String, DataSourceConfig>> it = allDatasourceConfigs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DataSourceConfig> entry = it.next();
            if (StringKits.isNotBlank(entry.getValue().getTable())) {
                hasTableDatasourceConfigs.put(entry.getKey(), entry.getValue());
                it.remove();
            }
        }

        // 优先创建有指定表的数据源的 activeRecordPlugin
        // 表一旦附着到 activeRecordPlugin， 就不会被其他 activeRecordPlugin 包含了
        createRecordPlugin(hasTableDatasourceConfigs);
        createRecordPlugin(allDatasourceConfigs);

    }

    private void createRecordPlugin(Map<String, DataSourceConfig> mergeDatasourceConfigs) {
        for (Map.Entry<String, DataSourceConfig> entry : mergeDatasourceConfigs.entrySet()) {
            DataSourceConfig datasourceConfig = entry.getValue();
            if (datasourceConfig.isConfigOk()) {
                ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(datasourceConfig);
                activeRecordPlugins.add(activeRecordPlugin);
            }
        }
    }


    /**
     * 创建 ActiveRecordPlugin 插件，用于数据库读写
     *
     * @param config
     * @return
     */
    public ActiveRecordPlugin createRecordPlugin(DataSourceConfig config) {

        ActiveRecordPlugin activeRecordPlugin = newRecordPlugin(config);

        if (StringKits.isNotBlank(config.getDbProFactory())) {
            activeRecordPlugin.setDbProFactory(ClassKits.newInstance(config.getDbProFactory()));
        }

        if (StringKits.isNotBlank(config.getContainerFactory())) {
            activeRecordPlugin.setContainerFactory(ClassKits.newInstance(config.getContainerFactory()));
        }

        if (config.getTransactionLevel() != null) {
            activeRecordPlugin.setTransactionLevel(config.getTransactionLevel());
        }

        activeRecordPlugin.setShowSql(Jboot.isDevMode());
        activeRecordPlugin.setDbProFactory(new JbootDbProFactory());

        JbootCache jbootCache = Jboot.getCache();
        if (jbootCache != null) {
            activeRecordPlugin.setCache(jbootCache);
        }

        configSqlTemplate(config, activeRecordPlugin);
        configDialect(activeRecordPlugin, config);

        /**
         * 不需要添加映射的直接返回
         *
         * 在一个表有多个数据源的情况下，应该只需要添加一个映射就可以了，
         * 添加映射：默认为该model的数据源，
         * 不添加映射：通过 model.use("xxx").save() 这种方式去调用该数据源
         * 不添加映射使用从场景一般是：读写分离时，用于读取只读数据库的数据
         */
        if (!config.isNeedAddMapping()) {
            return activeRecordPlugin;
        }

        //获得该数据源匹配的表
        List<TableInfo> tableInfos = TableInfoManager.me().getMatchTablesInfos(config);
        if (ArrayKits.isNullOrEmpty(tableInfos)) {
            return activeRecordPlugin;
        }

        for (TableInfo ti : tableInfos) {
            if (StringKits.isNotBlank(ti.getPrimaryKey())) {
                activeRecordPlugin.addMapping(ti.getTableName(), ti.getPrimaryKey(), (Class<? extends Model<?>>) ti.getModelClass());
            } else {
                activeRecordPlugin.addMapping(ti.getTableName(), (Class<? extends Model<?>>) ti.getModelClass());
            }
        }

        return activeRecordPlugin;
    }

    private ActiveRecordPlugin newRecordPlugin(DataSourceConfig config) {

        String configName = config.getName();
        DataSource dataSource = new DataSourceBuilder(config).build();

        String clazzName = config.getActiveRecordPluginClass();
        if (StringKits.isBlank(clazzName)) {
            return StringKits.isNotBlank(configName)
                    ? new ActiveRecordPlugin(configName, dataSource)
                    : new ActiveRecordPlugin(dataSource);
        }

        try {
            Class<ActiveRecordPlugin> arpc = (Class<ActiveRecordPlugin>) Class.forName(clazzName, false, Thread.currentThread().getContextClassLoader());
            if (StringKits.isNotBlank(configName)) {
                Constructor constructor = arpc.getConstructor(String.class, DataSource.class);
                return (ActiveRecordPlugin) constructor.newInstance(configName, dataSource);
            } else {
                Constructor constructor = arpc.getConstructor(DataSource.class);
                return (ActiveRecordPlugin) constructor.newInstance(dataSource);
            }
        } catch (Exception e) {
            throw new JbootException(e.toString(), e);
        }
    }


    /**
     * 配置 本地 sql
     *
     * @param datasourceConfig
     * @param activeRecordPlugin
     */
    private void configSqlTemplate(DataSourceConfig datasourceConfig, ActiveRecordPlugin activeRecordPlugin) {
        String sqlTemplatePath = datasourceConfig.getSqlTemplatePath();
        if (StringKits.isNotBlank(sqlTemplatePath)) {
            if (sqlTemplatePath.startsWith("/")) {
                activeRecordPlugin.setBaseSqlTemplatePath(datasourceConfig.getSqlTemplatePath());
            } else {
                activeRecordPlugin.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/" + datasourceConfig.getSqlTemplatePath());
            }
        } else {
            activeRecordPlugin.setBaseSqlTemplatePath(PathKit.getRootClassPath());
        }


        String sqlTemplateString = datasourceConfig.getSqlTemplate();
        if (sqlTemplateString != null) {
            String[] sqlTemplateFiles = sqlTemplateString.split(",");
            for (String sql : sqlTemplateFiles) {
                activeRecordPlugin.addSqlTemplate(sql);
            }
        }
    }

    /**
     * 配置 数据源的 方言
     *
     * @param activeRecordPlugin
     * @param datasourceConfig
     */
    private void configDialect(ActiveRecordPlugin activeRecordPlugin, DataSourceConfig datasourceConfig) {

        if (datasourceConfig.getDialectClass() != null) {
            Dialect dialect = ClassKits.newInstance(datasourceConfig.getDialectClass(), false);
            if (dialect == null) {
                throw new NullPointerException("can not new instance by class:" + datasourceConfig.getDialectClass());
            }
            activeRecordPlugin.setDialect(dialect);
            return;
        }

        switch (datasourceConfig.getType()) {
            case DataSourceConfig.TYPE_MYSQL:
                activeRecordPlugin.setDialect(new JbootMysqlDialect());
                break;
            case DataSourceConfig.TYPE_ORACLE:
                if (StringKits.isBlank(datasourceConfig.getContainerFactory())) {
                    activeRecordPlugin.setContainerFactory(new CaseInsensitiveContainerFactory());
                }
                activeRecordPlugin.setDialect(new JbootOracleDialect());
                break;
            case DataSourceConfig.TYPE_SQLSERVER:
                activeRecordPlugin.setDialect(new JbootSqlServerDialect());
                break;
            case DataSourceConfig.TYPE_SQLITE:
                activeRecordPlugin.setDialect(new JbootSqlite3Dialect());
                break;
            case DataSourceConfig.TYPE_ANSISQL:
                activeRecordPlugin.setDialect(new JbootAnsiSqlDialect());
                break;
            case DataSourceConfig.TYPE_POSTGRESQL:
                activeRecordPlugin.setDialect(new JbootPostgreSqlDialect());
                break;
            default:
                throw new JbootIllegalConfigException("only support datasource type : mysql、orcale、sqlserver、sqlite、ansisql and postgresql, please check your jboot.properties. ");
        }
    }


    public List<ActiveRecordPlugin> getActiveRecordPlugins() {
        return activeRecordPlugins;
    }

}
