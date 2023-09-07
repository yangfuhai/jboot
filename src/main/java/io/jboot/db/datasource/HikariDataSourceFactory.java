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
package io.jboot.db.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;

import javax.sql.DataSource;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class HikariDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(DataSourceConfig config) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", config.isCachePrepStmts());
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", config.getPrepStmtCacheSize());
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", config.getPrepStmtCacheSqlLimit());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setPoolName(config.getPoolName());
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());

        if (config.getMaxLifetime() != null) {
            hikariConfig.setMaxLifetime(config.getMaxLifetime());
        }
        if (config.getIdleTimeout() != null) {
            hikariConfig.setIdleTimeout(config.getIdleTimeout());
        }

        if (config.getMinimumIdle() != null) {
            hikariConfig.setMinimumIdle(config.getMinimumIdle());
        }

        if (config.getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(config.getConnectionInitSql());
        }

        if(config.getKeepaliveTime() != null){
            hikariConfig.setKeepaliveTime(config.getKeepaliveTime());
        }

        if(config.getValidationQuery() != null){
            hikariConfig.setConnectionTestQuery(config.getValidationQuery());
        }

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        if (Jboot.getMetric() != null) {
            dataSource.setMetricRegistry(Jboot.getMetric());
        }

        return dataSource;
    }
}
