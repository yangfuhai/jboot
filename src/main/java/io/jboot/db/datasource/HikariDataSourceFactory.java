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
package io.jboot.db.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;

import javax.sql.DataSource;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.datasource
 */
public class HikariDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(DataSourceConfig dataSourceConfig) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dataSourceConfig.getUrl());
        hikariConfig.setUsername(dataSourceConfig.getUser());
        hikariConfig.setPassword(dataSourceConfig.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", dataSourceConfig.isCachePrepStmts());
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", dataSourceConfig.getPrepStmtCacheSize());
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", dataSourceConfig.getPrepStmtCacheSqlLimit());

        hikariConfig.setDriverClassName(dataSourceConfig.getDriverClassName());
        hikariConfig.setPoolName(dataSourceConfig.getPoolName());


        if (dataSourceConfig.getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(dataSourceConfig.getConnectionInitSql());
        }


        hikariConfig.setMaximumPoolSize(dataSourceConfig.getMaximumPoolSize());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        dataSource.setMetricRegistry(Jboot.me().getMetric());

        return dataSource;
    }
}
