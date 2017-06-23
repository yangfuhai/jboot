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
package io.jboot.core.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class JbootSpiManager {

    private static final JbootSpiManager me = new JbootSpiManager();

    private JbootSpiManager() {
    }

    public static JbootSpiManager me() {
        return me;
    }

    public <T> T spi(Class<T> clazz, String spiName) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (spiName == null) {
                return t;
            } else {
                JbootSpi spi = t.getClass().getAnnotation(JbootSpi.class);
                if (spiName.equals(spi.value())) {
                    return t;
                }
            }

        }
        return null;
    }
}
