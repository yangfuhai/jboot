/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;

public class DriverClassNames {

    private static final Map<String, String[]> driverClassNames = new HashMap<>();

    static {
        driverClassNames.put(DataSourceConfig.TYPE_MYSQL, new String[]{"com.mysql.jdbc.Driver", "com.mysql.cj.jdbc.Driver"});
        driverClassNames.put(DataSourceConfig.TYPE_ORACLE, new String[]{"oracle.jdbc.driver.OracleDriver", "oracle.jdbc.OracleDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_SQLSERVER, new String[]{"com.microsoft.sqlserver.jdbc.SQLServerDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_SQLITE, new String[]{"org.sqlite.JDBC"});
        driverClassNames.put(DataSourceConfig.TYPE_POSTGRESQL, new String[]{"org.postgresql.Driver"});
    }


    /**
     * 获取 默认的 jdbc 驱动类
     * @param type
     * @return
     */
    public static String getDefaultDriverClass(String type) {
        String[] drivers = driverClassNames.get(type);
        if (drivers == null || drivers.length == 0) {
            return null;
        }

        for (String driver : drivers) {
            if (ClassUtil.hasClass(driver)) {
                return driver;
            }
        }
        return null;
    }


}
