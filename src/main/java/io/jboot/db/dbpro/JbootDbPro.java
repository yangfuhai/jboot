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
package io.jboot.db.dbpro;

import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.SqlDebugger;
import io.jboot.db.dialect.IJbootModelDialect;
import io.jboot.db.model.Columns;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.dbpro
 */
public class JbootDbPro extends DbPro {

    public JbootDbPro() {
    }


    public JbootDbPro(String configName) {
        super(configName);
    }


    @Override
    protected List<Record> find(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        SqlDebugger.debug(config, sql, paras);
        return super.find(config, conn, sql, paras);
    }


    @Override
    protected <T> List<T> query(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        SqlDebugger.debug(config, sql, paras);
        return super.query(config, conn, sql, paras);
    }


    @Override
    public int update(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        SqlDebugger.debug(config, sql, paras);
        return super.update(config, conn, sql, paras);
    }

    public List<Record> find(String tableName, Columns columns) {
        return find(tableName, columns, null, null);
    }


    public List<Record> find(String tableName, Columns columns, String orderBy) {
        return find(tableName, columns, orderBy, null);
    }


    public List<Record> find(String tableName, Columns columns, Object limit) {
        return find(tableName, columns, null, limit);
    }


    public List<Record> find(String tableName, Columns columns, String orderBy, Object limit) {
        IJbootModelDialect dialect = (IJbootModelDialect) getConfig().getDialect();
        String sql = dialect.forFindByColumns(tableName, "*", columns.getList(), orderBy, limit);
        return columns.isEmpty() ? find(sql) : find(sql, columns.getValueArray());
    }


    public int delete(String tableName, Columns columns) {
        IJbootModelDialect dialect = (IJbootModelDialect) getConfig().getDialect();
        String sql = dialect.forDeleteByColumns(tableName, columns.getList());
        return columns.isEmpty() ? delete(sql) : delete(sql, columns.getValueArray());
    }

}
