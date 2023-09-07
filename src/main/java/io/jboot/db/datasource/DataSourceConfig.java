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

import com.jfinal.plugin.activerecord.DbKit;
import io.jboot.db.TableInfo;
import io.jboot.db.driver.DriverClassNames;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;


public class DataSourceConfig {
    public static final String NAME_DEFAULT = DbKit.MAIN_CONFIG_NAME;

    public static final String TYPE_MYSQL = "mysql";
    public static final String TYPE_ORACLE = "oracle";
    public static final String TYPE_SQLSERVER = "sqlserver";
    public static final String TYPE_SQLITE = "sqlite";
    public static final String TYPE_ANSISQL = "ansisql";
    public static final String TYPE_POSTGRESQL = "postgresql";
    public static final String TYPE_DM = "dm";
    public static final String TYPE_CLICKHOUSE = "clickhouse";
    public static final String TYPE_INFORMIX = "informix";


    private String name;
    private String type = TYPE_MYSQL;
    private String url;
    private String user;
    private String password;
    private String driverClassName;
    private String connectionInitSql;
    private String poolName;
    private boolean cachePrepStmts = true;
    private int prepStmtCacheSize = 500;
    private int prepStmtCacheSqlLimit = 2048;
    private int maximumPoolSize = 10;

    private Long maxLifetime;
    private Long idleTimeout;
    private Integer minimumIdle = 0;

    // 配置获取连接等待超时的时间
    private long maxWait = -1;

    // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    private long timeBetweenEvictionRunsMillis = 60 * 1000L;
    // 配置连接在池中最小生存的时间
    private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;
    // 配置发生错误时多久重连
    private long timeBetweenConnectErrorMillis = 500;
    private String validationQuery;
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;

    private String sqlTemplatePath;
    private String sqlTemplate;
    private String factory; //HikariDataSourceFactory.class.getName();

    private String shardingConfigYaml;

    private String dbProFactory;
    private String containerFactory;
    private Integer transactionLevel;

    private String table; //此数据源包含哪些表
    private String exTable; //该数据源排除哪些表
    private String tablePrefix; //表前缀，假设 @Table(tableName="xxx")，那么实际表名为：tablePrefix + tableName

    private String dialectClass;
    private String activeRecordPluginClass;

    /**
     * HikariCP 连接探活间隔时间 4.0.1以上版本支持，建议设置为10分钟以内
     */
    private Long keepaliveTime;
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
        if (StrUtil.isNotBlank(driverClassName)) {
            return driverClassName;
        }
        return DriverClassNames.getDefaultDriverClass(getType());
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
        return StrUtil.isNotBlank(getUrl()) || StrUtil.isNotBlank(getShardingConfigYaml());
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

    public String getShardingConfigYaml() {
        return shardingConfigYaml;
    }

    public void setShardingConfigYaml(String shardingConfigYaml) {
        this.shardingConfigYaml = shardingConfigYaml;
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

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public Long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(Long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public Long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(Integer minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public String getDialectClass() {
        return dialectClass;
    }

    public void setDialectClass(String dialectClass) {
        this.dialectClass = dialectClass;
    }

    public String getActiveRecordPluginClass() {
        return activeRecordPluginClass;
    }

    public void setActiveRecordPluginClass(String activeRecordPluginClass) {
        this.activeRecordPluginClass = activeRecordPluginClass;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getTimeBetweenConnectErrorMillis() {
        return timeBetweenConnectErrorMillis;
    }

    public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
    }

    public String getValidationQuery() {
        if (validationQuery != null) {
            return validationQuery;
        }
        if (this.url == null) {
            return null;
        }
        String url = this.url.toLowerCase();
        if (url.startsWith("jdbc:oracle")) {
            return "select 1 from dual";
        } else if (url.startsWith("jdbc:db2")) {
            return "select 1 from sysibm.sysdummy1";
        } else if (url.startsWith("jdbc:hsqldb")) {
            return "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        } else if (url.startsWith("jdbc:derby")) {
            return "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        }
        return "select 1";
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }


    private List<TableInfo> tableInfos;

    /**
     * 添加表信息
     *
     * @param tableInfo      表信息
     * @param fromDesignated 是否是通过 jboot.datasource.table 或者 @table(datasource="xxx") 来指定的
     */
    public void addTableInfo(TableInfo tableInfo, boolean fromDesignated) {
        if (tableInfos == null) {
            tableInfos = new ArrayList<>();
        }

        if (!tableInfos.contains(tableInfo) && tableInfo.addAttachedDatasource(this, fromDesignated)) {
            tableInfos.add(tableInfo);
        }
    }

    public void removeTableInfo(TableInfo tableInfo) {
        if (tableInfos != null) {
            tableInfos.remove(tableInfo);
        }
    }


    public List<TableInfo> getTableInfos() {
        return tableInfos;
    }

    public Long getKeepaliveTime() {
        return keepaliveTime;
    }

    public void setKeepaliveTime(Long keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
    }
}
