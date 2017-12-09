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
package io.jboot.db;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.Jboot;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.db.dialect.*;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StringUtils;

import javax.sql.DataSource;
import java.util.*;


/**
 * 数据库 管理
 */
public class JbootDbManager {
    private static JbootDbManager manager;


    private List<ActiveRecordPlugin> activeRecordPlugins = new ArrayList<>();


    public static JbootDbManager me() {
        if (manager == null) {
            manager = ClassKits.singleton(JbootDbManager.class);
        }
        return manager;
    }

    public JbootDbManager() {

        // 所有的数据源
        Map<String, DataSourceConfig> datasourceConfigs = DataSourceConfigManager.me().getDatasourceConfigs();

        // 分库的数据源，一个数据源包含了多个数据源。
        Map<String, DataSourceConfig> shardingDatasourceConfigs = DataSourceConfigManager.me().getShardingDatasourceConfigs();

        if (shardingDatasourceConfigs != null && shardingDatasourceConfigs.size() > 0) {
            for (Map.Entry<String, DataSourceConfig> entry : shardingDatasourceConfigs.entrySet()) {
                String databaseConfig = entry.getValue().getShardingDatabase();
                if (StringUtils.isBlank(databaseConfig)) {
                    continue;
                }
                Set<String> databases = StringUtils.splitToSet(databaseConfig, ",");
                for (String database : databases) {
                    DataSourceConfig datasourceConfig = datasourceConfigs.remove(database);
                    if (datasourceConfig == null) {
                        throw new NullPointerException("has no datasource config named " + database + ",plase check your sharding database config");
                    }
                    entry.getValue().addChildDatasourceConfig(datasourceConfig);
                }
            }
        }

        //所有数据源，包含了分库的和未分库的
        Map<String, DataSourceConfig> allDatasourceConfigs = new HashMap<>();
        if (datasourceConfigs != null) {
            allDatasourceConfigs.putAll(datasourceConfigs);
        }

        if (shardingDatasourceConfigs != null) {
            allDatasourceConfigs.putAll(shardingDatasourceConfigs);
        }


        for (Map.Entry<String, DataSourceConfig> entry : allDatasourceConfigs.entrySet()) {

            DataSourceConfig datasourceConfig = entry.getValue();

            if (datasourceConfig.isConfigOk()) {

                ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(datasourceConfig);
                activeRecordPlugin.setShowSql(Jboot.me().isDevMode());
                activeRecordPlugin.setCache(Jboot.me().getCache());

                configSqlTemplate(datasourceConfig, activeRecordPlugin);
                configDialect(activeRecordPlugin, datasourceConfig);

                activeRecordPlugins.add(activeRecordPlugin);
            }
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
        if (sqlTemplatePath != null) {
            if (sqlTemplatePath.startsWith("/")) {
                activeRecordPlugin.setBaseSqlTemplatePath(datasourceConfig.getSqlTemplatePath());
            } else {
                activeRecordPlugin.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/" + datasourceConfig.getSqlTemplatePath());
            }
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
        switch (datasourceConfig.getType()) {
            case DataSourceConfig.TYPE_MYSQL:
                activeRecordPlugin.setDialect(new JbootMysqlDialect());
                break;
            case DataSourceConfig.TYPE_ORACLE:
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
                throw new JbootException("only support datasource type : mysql，orcale，sqlserver，sqlite，ansisql，postgresql");
        }
    }


    /**
     * 创建 ActiveRecordPlugin 插件，用于数据库读写
     *
     * @param config
     * @return
     */
    private ActiveRecordPlugin createRecordPlugin(DataSourceConfig config) {

        String configName = config.getName();
        DataSource dataSource = new DataSourceBuilder(config).build();
        String configTableString = config.getTable();
        String excludeTableString = config.getExcludeTable();

        ActiveRecordPlugin activeRecordPlugin = StringUtils.isNotBlank(configName)
                ? new ActiveRecordPlugin(configName, dataSource)
                : new ActiveRecordPlugin(dataSource);

        /**
         * 不需要添加映射的直接返回
         */
        if (!config.isNeedAddMapping()) {
            return activeRecordPlugin;
        }

        List<TableInfo> tableInfos = TableInfoManager.me().getTablesInfos(configTableString, excludeTableString);
        if (ArrayUtils.isNullOrEmpty(tableInfos)) {
            return activeRecordPlugin;
        }

        for (TableInfo ti : tableInfos) {
            if (StringUtils.isNotBlank(ti.getPrimaryKey())) {
                activeRecordPlugin.addMapping(ti.getTableName(), ti.getPrimaryKey(), (Class<? extends Model<?>>) ti.getModelClass());
            } else {
                activeRecordPlugin.addMapping(ti.getTableName(), (Class<? extends Model<?>>) ti.getModelClass());
            }
        }

        return activeRecordPlugin;
    }


    public List<ActiveRecordPlugin> getActiveRecordPlugins() {
        return activeRecordPlugins;
    }

}
