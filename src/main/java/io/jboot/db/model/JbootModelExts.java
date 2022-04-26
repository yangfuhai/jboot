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
package io.jboot.db.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JbootModelExts {

    private static Map<Integer, Map<String, Object>> extAttrs = new ConcurrentHashMap<>();

    private static final String DISTINCT = "distinct";


    public static Object getExtAttr(Object holder, String attr) {
        Map<String, Object> map = extAttrs.get(System.identityHashCode(holder));
        return map != null ? map.get(attr) : null;
    }


    public static void setExtAttr(Object holder, String attr, Object value) {
        Map<String, Object> map = extAttrs.get(System.identityHashCode(holder));
        if (map == null) {
            synchronized (holder) {
                map = extAttrs.get(System.identityHashCode(holder));
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    extAttrs.put(System.identityHashCode(holder), map);
                }
            }
        }

        map.put(attr, value);
    }


    public static void setDistinctColumn(JbootModel<?> holder, String column) {
        setExtAttr(holder, DISTINCT, column);
    }

    public static String getDistinctColumn(JbootModel<?> holder) {
        return (String) getExtAttr(holder, DISTINCT);
    }


}
