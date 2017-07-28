/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
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
import com.jfinal.plugin.activerecord.dialect.*;
import io.jboot.Jboot;
import io.jboot.db.annotation.Table;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DatasourceConfig;
import io.jboot.db.datasource.DatasourceConfigManager;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 数据库 管理
 */
public class JbootDbManager {
    private static JbootDbManager manager;


    private List<ActiveRecordPlugin> activeRecordPlugins = new ArrayList<>();


    public static JbootDbManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootDbManager.class);
        }
        return manager;
    }

    private JbootDbManager() {

        List<DatasourceConfig> datasourceConfigs = DatasourceConfigManager.me().getDatasourceConfigs();
        for (DatasourceConfig datasourceConfig : datasourceConfigs) {
            if (datasourceConfig.isConfigOk()) {
                DataSourceBuilder dsBuilder = new DataSourceBuilder(datasourceConfig);
                ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(datasourceConfig.getName(), datasourceConfig.getTable(), dsBuilder.build());
                activeRecordPlugin.setShowSql(Jboot.me().isDevMode());
                activeRecordPlugin.setCache(Jboot.me().getCache());

                initActiveRecordPluginDialect(activeRecordPlugin, datasourceConfig);

                activeRecordPlugins.add(activeRecordPlugin);
            }
        }

    }

    private void initActiveRecordPluginDialect(ActiveRecordPlugin activeRecordPlugin, DatasourceConfig datasourceConfig) {
        switch (datasourceConfig.getType()) {
            case DatasourceConfig.TYPE_MYSQL:
                activeRecordPlugin.setDialect(new MysqlDialect());
                break;
            case DatasourceConfig.TYPE_ORACLE:
                activeRecordPlugin.setDialect(new OracleDialect());
                break;
            case DatasourceConfig.TYPE_SQLSERVER:
                activeRecordPlugin.setDialect(new SqlServerDialect());
                break;
            case DatasourceConfig.TYPE_SQLITE:
                activeRecordPlugin.setDialect(new Sqlite3Dialect());
                break;
            case DatasourceConfig.TYPE_ANSISQL:
                activeRecordPlugin.setDialect(new AnsiSqlDialect());
                break;
            case DatasourceConfig.TYPE_POSTGRESQL:
                activeRecordPlugin.setDialect(new PostgreSqlDialect());
                break;
        }

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
     * 创建 ActiveRecordPlugin 插件，用于数据库读写
     *
     * @param configName
     * @param configTable 指定只做哪些表的映射关系
     * @return
     */
    private ActiveRecordPlugin createRecordPlugin(String configName, String configTable, DataSource dataSource) {

        List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);

        if (ArrayUtils.isNullOrEmpty(modelClassList)) {
            return null;
        }

        ActiveRecordPlugin activeRecordPlugin = StringUtils.isNotBlank(configName)
                ? new ActiveRecordPlugin(configName, dataSource)
                : new ActiveRecordPlugin(dataSource);

        Set<String> tables = configTable == null ? null : buildTables(configTable);

        for (Class<?> clazz : modelClassList) {
            Table tb = clazz.getAnnotation(Table.class);
            if (tb == null)
                continue;

            //说明该数据源只允许部分表
            if (tables != null && !tables.isEmpty()) {

                //如果该数据源的表配置不包含该表，过滤掉
                if (!tables.contains(tb.tableName())) {
                    continue;
                }
            }

            if (StringUtils.isNotBlank(tb.primaryKey())) {
                activeRecordPlugin.addMapping(tb.tableName(), tb.primaryKey(), (Class<? extends Model<?>>) clazz);
            } else {
                activeRecordPlugin.addMapping(tb.tableName(), (Class<? extends Model<?>>) clazz);
            }
        }

        return activeRecordPlugin;
    }

    private Set<String> buildTables(String configTable) {
        String[] tables = configTable.split(",");

        Set<String> tableset = new HashSet<>();
        for (String table : tables) {
            if (StringUtils.isBlank(table)) {
                continue;
            }
            tableset.add(table.trim());
        }
        return tableset;
    }

    public List<ActiveRecordPlugin> getActiveRecordPlugins() {
        return activeRecordPlugins;
    }

}
