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
package io.jboot.db;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Config;
import io.jboot.Jboot;
import io.jboot.utils.DateUtil;
import io.jboot.utils.StrUtil;

import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/12
 */
public class SqlDebugger {


    private static SqlDebugPrinter printer = SqlDebugPrinter.DEFAULT_PRINTER;

    public static SqlDebugPrinter getPrinter() {
        return printer;
    }

    public static void setPrinter(SqlDebugPrinter printer) {
        SqlDebugger.printer = printer;
    }

    public static <T> T run(SqlRunner<T> runner, Config config, String sql, Object... paras) throws SQLException {
        if (!printer.isPrintEnable(config)) {
            return runner.run();
        } else {
            long timeMillis = System.currentTimeMillis();
            try {
                return runner.run();
            } finally {
                doDebug(System.currentTimeMillis() - timeMillis, sql, paras);
            }
        }
    }


    private static void doDebug(Long tookTimeMillis, String sql, Object... paras) {
        if (paras != null) {
            for (Object value : paras) {
                // null
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "null");
                }
                // number
                else if (value instanceof Number || value instanceof Boolean) {
                    sql = sql.replaceFirst("\\?", value.toString());
                }
                // numeric
                else if (value instanceof String && StrUtil.isNumeric((String) value)) {
                    sql = sql.replaceFirst("\\?", (String) value);
                }
                // other
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("'");
                    if (value instanceof Date) {
                        sb.append(DateUtil.toDateTimeString((Date) value));
                    } else {
                        sb.append(value);
                    }
                    sb.append("'");
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(sb.toString()));
                }
            }
        }

        printer.print(sql, tookTimeMillis);
    }


    public interface SqlDebugPrinter {

        SqlDebugPrinter DEFAULT_PRINTER = new SqlDebugPrinter() {

            private boolean printSqlEnable = Jboot.isDevMode();

            @Override
            public void setPrintEnable(boolean enable) {
                this.printSqlEnable = enable;
            }

            @Override
            public boolean isPrintEnable(Config config) {
                return printSqlEnable;
            }

            @Override
            public void print(String sql, Long tookTimeMillis) {
                if (tookTimeMillis != null) {
                    System.out.println("Jboot exec sql took " + tookTimeMillis + " ms >>>  " + sql);
                } else {
                    System.out.println("Jboot exec sql >>>  " + sql);
                }
            }
        };

        SqlDebugPrinter LOG_PRINTER = new SqlDebugPrinter() {

            private boolean printSqlEnable = Jboot.isDevMode();
            private Log log = Log.getLog("SqlDebugPrinter.LogPrinter");

            @Override
            public void setPrintEnable(boolean enable) {
                this.printSqlEnable = enable;
            }

            @Override
            public boolean isPrintEnable(Config config) {
                return printSqlEnable;
            }

            @Override
            public void print(String sql, Long tookTimeMillis) {
                if (tookTimeMillis != null) {
                    log.debug("Jboot exec sql took " + tookTimeMillis + " ms >>>  " + sql);
                } else {
                    log.debug("Jboot exec sql >>>  " + sql);
                }
            }
        };

        void setPrintEnable(boolean enable);

        boolean isPrintEnable(Config config);

        void print(String sql, Long tookTimeMillis);
    }

    public interface SqlRunner<V> {
        V run() throws SQLException;
    }
}
