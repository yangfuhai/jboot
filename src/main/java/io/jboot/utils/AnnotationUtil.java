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


import io.jboot.app.config.JbootConfigKit;

public class AnnotationUtil {

    public static String get(String value) {
        return get(value, StrUtil.EMPTY);
    }

    public static String get(String value, String defaultValue) {
        if (StrUtil.isNotBlank(value)) {
            String ret = JbootConfigKit.parseValue(value.trim());
            if (StrUtil.isNotBlank(ret)) {
                return ret;
            }
        }
        return defaultValue;
    }


    public static Integer getInt(String value) {
        String intValue = get(value);
        if (intValue == null) return null;
        return Integer.valueOf(intValue);
    }

    public static Integer getInt(String value, int defaultValue) {
        String intValue = get(value);
        if (intValue == null) return defaultValue;
        return Integer.valueOf(intValue);
    }

    public static Long getLong(String value) {
        String longValue = get(value);
        if (longValue == null) return null;
        return Long.valueOf(longValue);
    }

    public static Long getLong(String value, long defaultValue) {
        String longValue = get(value);
        if (longValue == null) return defaultValue;
        return Long.valueOf(longValue);
    }

    public static Boolean getBool(String value) {
        String boolValue = get(value);
        if (boolValue == null) return null;
        return Boolean.valueOf(boolValue);
    }

    public static Boolean getBool(String value, boolean defaultValue) {
        String boolValue = get(value);
        if (boolValue == null) return defaultValue;
        return Boolean.valueOf(boolValue);
    }

    public static String[] get(String[] value) {
        if (ArrayUtil.isNullOrEmpty(value)) {
            return null;
        }

        String[] rets = new String[value.length];
        for (int i = 0; i < rets.length; i++) {
            rets[i] = get(value[i]);
        }
        return rets;
    }


}
