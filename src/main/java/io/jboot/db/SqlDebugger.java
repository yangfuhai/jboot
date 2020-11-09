/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Config;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;

import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/12
 */
public class SqlDebugger {

    private static SqlDebugPrinter defaultPrinter = new SqlDebugPrinter() {

        @Override
        public boolean isPrintEnable(Config config) {
            return Jboot.isDevMode();
        }

        @Override
        public void print(String sql, Long takedTimeMillis) {
            if (takedTimeMillis != null) {
                System.out.println("Jboot exec sql taked " + takedTimeMillis + " ms >>>  " + sql);
            } else {
                System.out.println("Jboot exec sql >>>  " + sql);
            }
        }
    };

    private static SqlDebugPrinter printer = defaultPrinter;

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



    private static void doDebug(Long takedTimeMillis, String sql, Object... paras) {
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
                        sb.append(DateKit.toStr((Date) value, DateKit.timeStampPattern));
                    } else {
                        sb.append(value.toString());
                    }
                    sb.append("'");
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(sb.toString()));
                }
            }
        }

        printer.print(sql, takedTimeMillis);
    }


    public static interface SqlDebugPrinter {
        public boolean isPrintEnable(Config config);
        public void print(String sql, Long takedTimeMillis);
    }

    public static interface SqlRunner<V> {
        V run() throws SQLException;
    }
}
