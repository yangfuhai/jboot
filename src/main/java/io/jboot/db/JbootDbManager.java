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
import io.jboot.db.datasource.ProxyDatasourceConfig;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import javax.sql.DataSource;
import java.util.List;


/**
 * 数据库 管理
 */
public class JbootDbManager {
    private static JbootDbManager manager;


    private DatasourceConfig datasourceConfig;
    private ProxyDatasourceConfig proxyDatasourceConfig;

    private ActiveRecordPlugin activeRecordPlugin;
    private ActiveRecordPlugin proxyActiveRecordPlugin;


    public static JbootDbManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootDbManager.class);
        }
        return manager;
    }

    private JbootDbManager() {

        datasourceConfig = Jboot.config(DatasourceConfig.class);

        if (datasourceConfig.isConfigOk()) {
            DataSourceBuilder dsBuilder = new DataSourceBuilder(datasourceConfig);
            activeRecordPlugin = createRecordPlugin(null, dsBuilder.build());
            activeRecordPlugin.setShowSql(Jboot.me().isDevMode());
            activeRecordPlugin.setCache(Jboot.me().getCache());
            initActiveRecordPluginDialect(activeRecordPlugin, datasourceConfig);
        }


        proxyDatasourceConfig = Jboot.config(ProxyDatasourceConfig.class);
        if (proxyDatasourceConfig.isConfigOk()) {
            DataSourceBuilder dsBuilder = new DataSourceBuilder(proxyDatasourceConfig);
            proxyActiveRecordPlugin = createRecordPlugin("proxy", dsBuilder.build());
            proxyActiveRecordPlugin.setShowSql(Jboot.me().isDevMode());
            proxyActiveRecordPlugin.setCache(Jboot.me().getCache());
            initActiveRecordPluginDialect(proxyActiveRecordPlugin, proxyDatasourceConfig);
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


    public boolean isConfigOk() {
        //return datasourceConfig.isConfigOk();
        return activeRecordPlugin != null;
    }

    public boolean isProxyConfigOk() {
        //return datasourceConfig.isConfigOk();
        return proxyActiveRecordPlugin != null;
    }

    /**
     * 获取 数据库插件 ActiveRecordPlugin
     *
     * @return
     */
    public ActiveRecordPlugin getActiveRecordPlugin() {
        return activeRecordPlugin;
    }

    public ActiveRecordPlugin getProxyActiveRecordPlugin() {
        return proxyActiveRecordPlugin;
    }

    /**
     * 创建 ActiveRecordPlugin 插件，用于数据库读写
     *
     * @param configName
     * @param dataSource
     * @return
     */
    private ActiveRecordPlugin createRecordPlugin(String configName, DataSource dataSource) {

        List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);

        if (ArrayUtils.isNullOrEmpty(modelClassList)) {
            return null;
        }

        ActiveRecordPlugin activeRecordPlugin = StringUtils.isNotBlank(configName)
                ? new ActiveRecordPlugin(configName, dataSource)
                : new ActiveRecordPlugin(dataSource);

        for (Class<?> clazz : modelClassList) {
            Table tb = clazz.getAnnotation(Table.class);
            if (tb == null)
                continue;
            if (StringUtils.isNotBlank(tb.primaryKey())) {
                activeRecordPlugin.addMapping(tb.tableName(), tb.primaryKey(), (Class<? extends Model<?>>) clazz);
            } else {
                activeRecordPlugin.addMapping(tb.tableName(), (Class<? extends Model<?>>) clazz);
            }
        }

        return activeRecordPlugin;
    }
}
