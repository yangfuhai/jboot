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
package io.jboot.db;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.dbpro.JbootDbPro;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;

import java.util.List;


public class JbootDb extends Db {

    private static ThreadLocal<String> CONFIG_NAME_TL = new ThreadLocal<>();

    public static String getCurentConfigName() {
        return CONFIG_NAME_TL.get();
    }

    public static void setCurrentConfigName(String configName) {
        CONFIG_NAME_TL.set(configName);
    }

    public static void clearCurrentConfigName() {
        CONFIG_NAME_TL.remove();
    }

    public static JbootDbPro use(String configName) {
        return (JbootDbPro) Db.use(configName);
    }

    public static JbootDbPro use() {
        String currentConfigName = getCurentConfigName();
        return StrUtil.isBlank(currentConfigName) ? (JbootDbPro) Db.use() : use(currentConfigName);
    }


    public static List<Record> find(String tableName, Columns columns) {
        return find(tableName, columns, null, null);
    }


    public static List<Record> find(String tableName, Columns columns, String orderBy) {
        return find(tableName, columns, orderBy, null);
    }


    public static List<Record> find(String tableName, Columns columns, Object limit) {
        return find(tableName, columns, null, limit);
    }


    public static List<Record> find(String tableName, Columns columns, String orderBy, Object limit) {
        return use().find(tableName, columns, orderBy, limit);
    }


    public static Record findFirst(String tableName, Columns columns) {
        return findFirst(tableName, columns, null);
    }


    public static Record findFirst(String tableName, Columns columns, String orderBy) {
        final List<Record> records = use().find(tableName, columns, orderBy, 1);
        return records != null && !records.isEmpty() ? records.get(0) : null;
    }


    public static int delete(String tableName, Columns columns) {
        return use().delete(tableName, columns);
    }


}
