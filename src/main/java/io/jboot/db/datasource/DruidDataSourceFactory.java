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

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Sets;
import com.jfinal.log.Log;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.datasource
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    static Log log = Log.getLog(DruidDataSourceFactory.class);

    @Override
    public DataSource createDataSource(DataSourceConfig dataSourceConfig) {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(dataSourceConfig.getUrl());
        druidDataSource.setUsername(dataSourceConfig.getUser());
        druidDataSource.setPassword(dataSourceConfig.getPassword());
        druidDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        try {
            druidDataSource.setFilters("stat");
        } catch (SQLException e) {
            log.error("DruidDataSourceFactory is error", e);
        }

        if (dataSourceConfig.getConnectionInitSql() != null) {
            druidDataSource.setConnectionInitSqls(Sets.newHashSet(dataSourceConfig.getConnectionInitSql()));
        }

        return druidDataSource;
    }
}
