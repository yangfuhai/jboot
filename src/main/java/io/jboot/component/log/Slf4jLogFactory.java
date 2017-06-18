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
package io.jboot.component.log;

import com.jfinal.log.ILogFactory;
import com.jfinal.log.JdkLog;
import com.jfinal.log.Log;

public class Slf4jLogFactory implements ILogFactory {


    private static Slf4jLogFactory factory;

    public static Slf4jLogFactory me() {
        if (factory == null) {
            factory = new Slf4jLogFactory();
            factory.slf4jIsOk = new Slf4jLogger("").isOk();
        }
        return factory;
    }

    private boolean slf4jIsOk;


    @Override
    public Log getLog(Class<?> clazz) {
        return slf4jIsOk ? new Slf4jLogger(clazz) : JdkLog.getLog(clazz);
    }

    @Override
    public Log getLog(String name) {
        return slf4jIsOk ? new Slf4jLogger(name) : JdkLog.getLog(name);
    }
}
