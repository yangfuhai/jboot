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


public class ArrayUtil {

    public static boolean isNotEmpty(Collection<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isNotEmpty(Map map) {
        return map != null && map.size() > 0;
    }

    public static boolean isNotEmpty(Object[] objects) {
        return objects != null && objects.length > 0;
    }

    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNullOrEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static <T> T[] concat(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static <T> boolean contains(T[] array, T element) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (T t : array) {
            if (Objects.equals(t, element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameElements(Collection<?> c1, Collection<?> c2) {
        if (c1 == c2) {
            return true;
        }

        if ((c1 == null || c1.isEmpty()) && (c2 == null || c2.isEmpty())) {
            return true;
        }

        if (c1 != null && c2 != null && c1.size() == c2.size() && c1.containsAll(c2)) {
            return true;
        }

        return false;
    }


    public static String toString(Object[] strings, String delimiter) {
        StringJoiner sb = new StringJoiner(delimiter);
        if (strings != null) {
            for (Object o : strings) {
                sb.add(String.valueOf(o));
            }
        }
        return sb.toString();
    }

}
