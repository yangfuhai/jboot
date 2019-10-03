/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.util.List;


public class JbootDb extends Db {


    public static JbootDbPro use(String configName) {
        return (JbootDbPro) Db.use(configName);
    }

    public static JbootDbPro use() {
        return (JbootDbPro) Db.use();
    }


    public static List<Record> find(String tableName, Columns columns) {
        return find(null, tableName, columns, null, null);
    }


    public static List<Record> find(String tableName, Columns columns, String orderBy) {
        return find(null, tableName, columns, orderBy, null);
    }


    public static List<Record> find(String tableName, Columns columns, Object limit) {
        return find(null, tableName, columns, null, limit);
    }


    public static List<Record> find(String tableName, Columns columns, String orderBy, Object limit) {
        return use().find(tableName,columns,orderBy,limit);
    }


    public static int delete(String tableName, Columns columns) {
        return use().delete(tableName,columns);
    }



}
