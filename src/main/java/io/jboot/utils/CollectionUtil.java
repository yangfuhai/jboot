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
package io.jboot.utils;

import java.util.*;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class CollectionUtil {

    public static Map<String, String> string2Map(String s) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] strings = s.split(",");
        for (String kv : strings) {
            if (kv != null && kv.contains(":")) {
                String[] keyValue = kv.split(":");
                if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return map;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    public static String toString(Collection<?> collection, String delimiter) {
        StringJoiner sb = new StringJoiner(delimiter);
        if (collection != null) {
            for (Object o : collection) {
                sb.add(String.valueOf(o));
            }
        }
        return sb.toString();
    }
}
