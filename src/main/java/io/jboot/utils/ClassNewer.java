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
package io.jboot.utils;

import com.jfinal.log.Log;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类实例创建者创建者
 * Created by michael on 17/3/21.
 */
public class ClassNewer {

    private static final Map<Class, Object> singletons = new ConcurrentHashMap<>();

    /**
     * 获取单例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T singleton(Class<T> clazz) {
        Object object = singletons.get(clazz);
        if (object == null) {
            synchronized (clazz) {
                object = singletons.get(clazz);
                if (object == null) {
                    object = newInstance(clazz);
                    if (object != null) {
                        singletons.put(clazz, object);
                    } else {
                        Log.getLog(clazz).error("cannot new newInstance!!!!");
                    }

                }
            }
        }

        return (T) object;
    }

    /**
     * 创建新的实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
