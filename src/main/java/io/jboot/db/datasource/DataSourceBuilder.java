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
package io.jboot.db.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;


public class DataSourceBuilder {

    private DatasourceConfig datasourceConfig;

    public DataSourceBuilder(DatasourceConfig datasourceConfig) {
        this.datasourceConfig = datasourceConfig;


    }

    public DataSource build() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(datasourceConfig.getUrl());
        hikariConfig.setUsername(datasourceConfig.getUser());
        hikariConfig.setPassword(datasourceConfig.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", datasourceConfig.isCachePrepStmts());
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", datasourceConfig.getPrepStmtCacheSize());
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", datasourceConfig.getPrepStmtCacheSqlLimit());

        hikariConfig.setDriverClassName(datasourceConfig.getDriverClassName());


        if (hikariConfig.getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(datasourceConfig.getConnectionInitSql());
        }


        hikariConfig.setMaximumPoolSize(datasourceConfig.getMaximumPoolSize());

        return new HikariDataSource(hikariConfig);
    }
}
