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

import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class DataSourceConfig {
    public static final String NAME_DEFAULT = "main";

    public static final String TYPE_MYSQL = "mysql";
    public static final String TYPE_ORACLE = "oracle";
    public static final String TYPE_SQLSERVER = "sqlserver";
    public static final String TYPE_SQLITE = "sqlite";
    public static final String TYPE_ANSISQL = "ansisql";
    public static final String TYPE_POSTGRESQL = "postgresql";

    private String name;
    private String type = TYPE_MYSQL;
    private String url;
    private String user;
    private String password;
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String connectionInitSql;
    private String poolName;
    private boolean cachePrepStmts = true;
    private int prepStmtCacheSize = 500;
    private int prepStmtCacheSqlLimit = 2048;
    private int maximumPoolSize = 100;

    private String sqlTemplatePath;
    private String sqlTemplate;
    private String factory; //HikariDataSourceFactory.class.getName();

    private boolean shardingEnable = false;
    private String shardingDatabase;


    private List<DataSourceConfig> childDatasourceConfigs;


    private String dbProFactory;
    private String containerFactory;
    private Integer transactionLevel;

    private String table; //此数据源包含哪些表，这个配置会覆盖@Table注解的配置
    private String exTable; //该数据源排除哪些表，这个配置会修改掉@Table上的配置

    /**
     * 是否需要添加到映射
     * 在一个表有多个数据源的情况下，应该只需要添加一个映射就可以了，
     * 添加映射：默认为该model的数据源，
     * 不添加映射：通过 model.use("xxx").save()这种方式去调用该数据源
     */
    private boolean needAddMapping = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }

    public void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
    }

    public boolean isCachePrepStmts() {
        return cachePrepStmts;
    }

    public void setCachePrepStmts(boolean cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public int getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    public void setPrepStmtCacheSize(int prepStmtCacheSize) {
        this.prepStmtCacheSize = prepStmtCacheSize;
    }

    public int getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    public void setPrepStmtCacheSqlLimit(int prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public boolean isConfigOk() {
        return (StringUtils.isNotBlank(url))
                || shardingEnable == true;
    }


    public boolean isMysqlType() {
        return TYPE_MYSQL.equals(getType());
    }

    public boolean isOracleType() {
        return TYPE_ORACLE.equals(getType());
    }

    public boolean isSqlServerType() {
        return TYPE_SQLSERVER.equals(getType());
    }

    public boolean isSqliteType() {
        return TYPE_SQLITE.equals(getType());
    }

    public boolean isAnsiSqlType() {
        return TYPE_ANSISQL.equals(getType());
    }

    public String getSqlTemplatePath() {
        return sqlTemplatePath;
    }

    public void setSqlTemplatePath(String sqlTemplatePath) {
        this.sqlTemplatePath = sqlTemplatePath;
    }

    public String getSqlTemplate() {
        return sqlTemplate;
    }

    public void setSqlTemplate(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public boolean isNeedAddMapping() {
        return needAddMapping;
    }

    public void setNeedAddMapping(boolean needAddMapping) {
        this.needAddMapping = needAddMapping;
    }

    public boolean isShardingEnable() {
        return shardingEnable;
    }

    public void setShardingEnable(boolean shardingEnable) {
        this.shardingEnable = shardingEnable;
    }

    public String getShardingDatabase() {
        return shardingDatabase;
    }

    public void setShardingDatabase(String shardingDatabase) {
        this.shardingDatabase = shardingDatabase;
    }

    public List<DataSourceConfig> getChildDatasourceConfigs() {
        return childDatasourceConfigs;
    }

    public void setChildDatasourceConfigs(List<DataSourceConfig> childDatasourceConfigs) {
        this.childDatasourceConfigs = childDatasourceConfigs;
    }

    public void addChildDatasourceConfig(DataSourceConfig config) {
        if (this.childDatasourceConfigs == null) {
            this.childDatasourceConfigs = new ArrayList<>();
        }

        this.childDatasourceConfigs.add(config);
    }

    public String getDbProFactory() {
        return dbProFactory;
    }

    public void setDbProFactory(String dbProFactory) {
        this.dbProFactory = dbProFactory;
    }

    public String getContainerFactory() {
        return containerFactory;
    }

    public void setContainerFactory(String containerFactory) {
        this.containerFactory = containerFactory;
    }

    public Integer getTransactionLevel() {
        return transactionLevel;
    }

    public void setTransactionLevel(Integer transactionLevel) {
        this.transactionLevel = transactionLevel;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getExTable() {
        return exTable;
    }

    public void setExTable(String exTable) {
        this.exTable = exTable;
    }
}
