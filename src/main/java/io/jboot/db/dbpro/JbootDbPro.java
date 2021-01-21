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
package io.jboot.db.dbpro;

import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import io.jboot.db.SqlDebugger;
import io.jboot.db.dialect.JbootClickHouseDialect;
import io.jboot.db.dialect.JbootDialect;
import io.jboot.db.model.Columns;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootDbPro extends DbPro {

    public JbootDbPro() {
    }


    public JbootDbPro(String configName) {
        super(configName);
    }


    @Override
    public List<Record> find(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return SqlDebugger.run(() -> super.find(config, conn, sql, paras), config, sql, paras);
    }


    @Override
    public <T> List<T> query(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return SqlDebugger.run(() -> super.query(config, conn, sql, paras), config, sql, paras);
    }


    @Override
    public int update(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return SqlDebugger.run(() -> super.update(config, conn, sql, paras), config, sql, paras);
    }


    @Override
    protected boolean save(Config config, Connection conn, String tableName, String primaryKey, Record record) throws SQLException {
        String[] pKeys = primaryKey.split(",");
        List<Object> paras = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder();

        Dialect dialect = config.getDialect();

        dialect.forDbSave(tableName, pKeys, record, sql, paras);

        //add sql debug support
        return SqlDebugger.run(() -> {
            PreparedStatement pst;
            if (dialect.isOracle()) {
                pst = conn.prepareStatement(sql.toString(), pKeys);
            }
            else if (dialect instanceof JbootClickHouseDialect){
                pst = conn.prepareStatement(sql.toString());
            }
            else {
                pst = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            }
            dialect.fillStatement(pst, paras);
            int result = pst.executeUpdate();
            dialect.getRecordGeneratedKey(pst, record, pKeys);

            if (pst != null) {
                pst.close();
            }

            return result >= 1;

        }, config, sql.toString(), paras.toArray());
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
        JbootDialect dialect = (JbootDialect) getConfig().getDialect();
        String sql = dialect.forFindByColumns(null, null, tableName, "*", columns.getList(), orderBy, limit);
        return columns.isEmpty() ? find(sql) : find(sql, columns.getValueArray());
    }


    public int delete(String tableName, Columns columns) {
        JbootDialect dialect = (JbootDialect) getConfig().getDialect();
        String sql = dialect.forDeleteByColumns(null, null, tableName, columns.getList());
        return columns.isEmpty() ? delete(sql) : delete(sql, columns.getValueArray());
    }


}
