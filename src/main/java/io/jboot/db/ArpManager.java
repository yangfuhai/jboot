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
package io.jboot.db;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.IDbProFactory;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.db.dbpro.JbootDbProFactory;
import io.jboot.db.dialect.*;
import io.jboot.db.record.JbootRecordBuilder;
import io.jboot.exception.JbootException;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.*;


/**
 * 数据库 管理
 */
public class ArpManager {

    private static ArpManager instance;


    private List<ActiveRecordPlugin> activeRecordPlugins = new ArrayList<>();


    public static ArpManager me() {
        if (instance == null) {
            instance = new ArpManager();
        }
        return instance;
    }

    private ArpManager() {
        Map<String, DataSourceConfig> datasourceConfigs = DataSourceConfigManager.me().getDatasourceConfigs();
        createRecordPlugin(datasourceConfigs);
    }

    private void createRecordPlugin(Map<String, DataSourceConfig> allConfigs) {

        Map<Integer, DataSourceConfig> arpDatasourceConfigs = new HashMap<>();

        for (Map.Entry<String, DataSourceConfig> entry : allConfigs.entrySet()) {
            DataSourceConfig datasourceConfig = entry.getValue();
            if (datasourceConfig.isConfigOk()) {

                // 执行 createRecordPlugin(...) 的时候，会同时完善 DataSourceConfig 里绑定的表数据
                // createRecordPlugin完毕后，就可以通过  dataSourceConfig.getTableInfos() 去获取该数据源有哪些表
                ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(datasourceConfig);

                arpDatasourceConfigs.put(System.identityHashCode(activeRecordPlugin), datasourceConfig);
                activeRecordPlugins.add(activeRecordPlugin);
            }
        }


        // 为 activeRecordPlugin 添加 jfinal 的表映射
        for (ActiveRecordPlugin activeRecordPlugin : activeRecordPlugins) {
            DataSourceConfig dataSourceConfig = arpDatasourceConfigs.get(System.identityHashCode(activeRecordPlugin));

            List<TableInfo> tableInfos = dataSourceConfig.getTableInfos();
            if (tableInfos != null && !tableInfos.isEmpty()) {
                for (TableInfo table : tableInfos) {
                    String tableName = StrUtil.isNotBlank(dataSourceConfig.getTablePrefix()) ? dataSourceConfig.getTablePrefix() + table.getTableName() : table.getTableName();
                    if (StrUtil.isNotBlank(table.getPrimaryKey())) {
                        activeRecordPlugin.addMapping(tableName, table.getPrimaryKey(), (Class<? extends Model<?>>) table.getModelClass());
                    } else {
                        activeRecordPlugin.addMapping(tableName, (Class<? extends Model<?>>) table.getModelClass());
                    }
                }
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

        if (StrUtil.isNotBlank(config.getDbProFactory())) {
            IDbProFactory dbProFactory = Objects.requireNonNull(ClassUtil.newInstance(config.getDbProFactory()),
                    "Can not create dbProfactory by class : " + config.getDbProFactory());
            activeRecordPlugin.setDbProFactory(dbProFactory);
        } else {
            activeRecordPlugin.setDbProFactory(new JbootDbProFactory());
        }

        if (StrUtil.isNotBlank(config.getContainerFactory())) {
            activeRecordPlugin.setContainerFactory(ClassUtil.newInstance(config.getContainerFactory()));
        }

        if (config.getTransactionLevel() != null) {
            activeRecordPlugin.setTransactionLevel(config.getTransactionLevel());
        }

        // 使用 Jboot 的 SqlDebugger 代替了
        activeRecordPlugin.setShowSql(false);

        JbootCache jbootCache = Jboot.getCache();
        if (jbootCache != null) {
            activeRecordPlugin.setCache(jbootCache);
        }

        configSqlTemplate(activeRecordPlugin, config);
        configDialect(activeRecordPlugin, config);

        //配置 Record 构建器
        activeRecordPlugin.getConfig().getDialect().setRecordBuilder(new JbootRecordBuilder());

        /**
         * 在一个表有多个数据源的情况下，应该只需要添加一个映射就可以了
         * 添加映射：默认为该 model 的数据源
         * 不添加映射：通过 model.use("xxx").save() 这种方式去调用该数据源
         * 不添加映射使用从场景一般是：读写分离时，用于读取只读数据库的数据
         */
        if (config.isNeedAddMapping()) {
            TableInfoManager.me().initConfigMappingTables(config);
        }

        return activeRecordPlugin;
    }

    private ActiveRecordPlugin newRecordPlugin(DataSourceConfig config) {

        String configName = config.getName();
        DataSource dataSource = new DataSourceBuilder(config).build();

        String clazzName = config.getActiveRecordPluginClass();
        if (StrUtil.isBlank(clazzName)) {
            return StrUtil.isNotBlank(configName)
                    ? new ActiveRecordPlugin(configName, dataSource)
                    : new ActiveRecordPlugin(dataSource);
        }

        try {
            Class<ActiveRecordPlugin> arpc = (Class<ActiveRecordPlugin>) Class.forName(clazzName, false, Thread.currentThread().getContextClassLoader());
            if (StrUtil.isNotBlank(configName)) {
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
    private void configSqlTemplate(ActiveRecordPlugin activeRecordPlugin, DataSourceConfig datasourceConfig) {
        String sqlTemplatePath = datasourceConfig.getSqlTemplatePath();
        if (StrUtil.isNotBlank(sqlTemplatePath)) {
            activeRecordPlugin.setBaseSqlTemplatePath(sqlTemplatePath);
        } else {
            activeRecordPlugin.setBaseSqlTemplatePath(null);
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
            Dialect dialect = ClassUtil.newInstance(datasourceConfig.getDialectClass(), false);
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
                if (StrUtil.isBlank(datasourceConfig.getContainerFactory())) {
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
            case DataSourceConfig.TYPE_CLICKHOUSE:
                activeRecordPlugin.setDialect(new JbootClickHouseDialect());
                break;
            default:
                throw new JbootIllegalConfigException("only support datasource type : mysql、orcale、sqlserver、sqlite、ansisql、postgresql and clickhouse, please check your jboot.properties. ");
        }
    }


    public List<ActiveRecordPlugin> getActiveRecordPlugins() {
        return activeRecordPlugins;
    }

}
