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

import io.jboot.utils.StringUtils;


public class DatasourceConfig {
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
    private String table;
    private String excludeTable;

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
        return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(user);
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getExcludeTable() {
        return excludeTable;
    }

    public void setExcludeTable(String excludeTable) {
        this.excludeTable = excludeTable;
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
}
