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

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.jfinal.log.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.db.sharding.IShardingRuleFactory;
import io.jboot.db.sharding.ShardingRuleFactoryBuilder;
import io.jboot.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;


public class DataSourceBuilder {

    static Log log = Log.getLog(DataSourceBuilder.class);

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
        hikariConfig.setPoolName(datasourceConfig.getPoolName());


        if (hikariConfig.getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(datasourceConfig.getConnectionInitSql());
        }


        hikariConfig.setMaximumPoolSize(datasourceConfig.getMaximumPoolSize());

        DataSource dataSource = new HikariDataSource(hikariConfig);

        // 如果 shardingRuleFacetory 配置为空，则不使用分表规则
        if (StringUtils.isBlank(datasourceConfig.getShardingRuleFactory())) {
            return dataSource;
        }

        // 如果 通过shardingRuleFacetory去创建IShardingRuleFactory 不成功，则不使用分表规则
        IShardingRuleFactory factory = ShardingRuleFactoryBuilder.me().build(datasourceConfig.getShardingRuleFactory());
        if (factory == null) {
            log.warn("create not create shardingRuleFactory");
            return dataSource;
        }

        ShardingRule shardingRule = factory.createShardingRule(dataSource);
        try {
            return ShardingDataSourceFactory.createDataSource(shardingRule);
        } catch (SQLException e) {
            log.error("create sharding datasource error." + e.toString(), e);
        }

        return dataSource;

    }
}
