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

import io.jboot.config.annotation.PropertieConfig;
import io.jboot.utils.StringUtils;

/**
 * Created by michael on 2017/5/10.
 */
@PropertieConfig(prefix = "jboot.datasource.master")
public class MasterDatasourceConfig extends DatasourceConfig {

    private String type = TYPE_MYSQL;
    private String url;
    private String user;
    private String password;
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String connectionInitSql;
    private String cachePrepStmts = "true";
    private String prepStmtCacheSize = "500";
    private String prepStmtCacheSqlLimit = "2048";


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

    public String getCachePrepStmts() {
        return cachePrepStmts;
    }

    public void setCachePrepStmts(String cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public String getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    public void setPrepStmtCacheSize(String prepStmtCacheSize) {
        this.prepStmtCacheSize = prepStmtCacheSize;
    }

    public String getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    public void setPrepStmtCacheSqlLimit(String prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }

    public boolean isConfigOk() {
        return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(user);
    }
}
