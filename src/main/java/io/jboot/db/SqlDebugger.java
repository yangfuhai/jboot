/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.core.JFinal;
import com.jfinal.ext.kit.DateKit;

import java.util.Date;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/12
 */
public class SqlDebugger {

    private static SqlDebugPrinter defaultPrinter = new SqlDebugPrinter() {

        private boolean isPrint = JFinal.me().getConstants().getDevMode();

        @Override
        public boolean isPrint() {
            return isPrint;
        }

        @Override
        public void print(String sql) {
            System.out.println("\r\nexec sql >>> " + sql);
        }
    };

    private static SqlDebugPrinter printer = defaultPrinter;

    public static SqlDebugPrinter getPrinter() {
        return printer;
    }

    public static void setPrinter(SqlDebugPrinter printer) {
        SqlDebugger.printer = printer;
    }

    public static void debug(String sql, Object... paras) {
        if (printer.isPrint()) {

            if (paras != null) {
                for (Object value : paras) {
                    String paraValue;
                    if (value instanceof Date) {
                        paraValue = DateKit.toStr((Date) value, DateKit.timeStampPattern);
                    } else {
                        paraValue = String.valueOf(value);
                    }
                    sql = sql.replaceFirst("\\?", paraValue);
                }
            }

            printer.print(sql);
        }
    }


    public static interface SqlDebugPrinter {

        public boolean isPrint();

        public void print(String sql);
    }

}
