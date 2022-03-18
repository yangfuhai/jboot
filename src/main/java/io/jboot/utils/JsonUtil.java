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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import io.jboot.web.json.JsonBodyParseInterceptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class JsonUtil {

    public static String getString(String json, String key) {
        return get(json, key, String.class);
    }

    public static String getString(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, String.class);
    }

    public static String getString(String json, String key, String defaultValue) {
        String value = getString(json, key);
        return value != null ? value : defaultValue;
    }

    public static String getString(Object jsonObjectOrArray, String key, String defaultValue) {
        String value = getString(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static Integer getInt(String json, String key) {
        return get(json, key, Integer.class);
    }

    public static Integer getInt(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, Integer.class);
    }

    public static int getInt(String json, String key, int defaultValue) {
        Integer value = getInt(json, key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(Object jsonObjectOrArray, String key, int defaultValue) {
        Integer value = getInt(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static Float getFloat(String json, String key) {
        return get(json, key, Float.class);
    }

    public static Float getFloat(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, Float.class);
    }

    public static float getFloat(String json, String key, float defaultValue) {
        Float value = getFloat(json, key);
        return value != null ? value : defaultValue;
    }

    public static float getFloat(Object jsonObjectOrArray, String key, float defaultValue) {
        Float value = getFloat(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static Double getDouble(String json, String key) {
        return get(json, key, Double.class);
    }

    public static Double getDouble(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, Double.class);
    }

    public static double getDouble(String json, String key, double defaultValue) {
        Double value = getDouble(json, key);
        return value != null ? value : defaultValue;
    }

    public static double getDouble(Object jsonObjectOrArray, String key, double defaultValue) {
        Double value = getDouble(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static Long getLong(String json, String key) {
        return get(json, key, Long.class);
    }

    public static Long getLong(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, Long.class);
    }

    public static long getLong(String json, String key, long defaultValue) {
        Long value = getLong(json, key);
        return value != null ? value : defaultValue;
    }

    public static long getLong(Object jsonObjectOrArray, String key, long defaultValue) {
        Long value = getLong(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static BigInteger getBigInteger(String json, String key) {
        return get(json, key, BigInteger.class);
    }

    public static BigInteger getBigInteger(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, BigInteger.class);
    }

    public static BigInteger getBigInteger(String json, String key, BigInteger defaultValue) {
        BigInteger value = getBigInteger(json, key);
        return value != null ? value : defaultValue;
    }


    public static BigInteger getBigInteger(Object jsonObjectOrArray, String key, BigInteger defaultValue) {
        BigInteger value = getBigInteger(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }

    public static BigDecimal getBigDecimal(String json, String key) {
        return get(json, key, BigDecimal.class);
    }

    public static BigDecimal getBigDecimal(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, BigDecimal.class);
    }

    public static BigDecimal getBigDecimal(String json, String key, BigDecimal defaultValue) {
        BigDecimal value = getBigDecimal(json, key);
        return value != null ? value : defaultValue;
    }


    public static BigDecimal getBigDecimal(Object jsonObjectOrArray, String key, BigDecimal defaultValue) {
        BigDecimal value = getBigDecimal(jsonObjectOrArray, key);
        return value != null ? value : defaultValue;
    }


    public static Date getDate(String json, String key) {
        return get(json, key, Date.class);
    }

    public static Date getDate(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, Date.class);
    }


    public static JSONObject getJSONObject(String json, String key) {
        return get(json, key, JSONObject.class);
    }

    public static JSONObject getJSONObject(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, JSONObject.class);
    }


    public static JSONArray getJSONArray(String json, String key) {
        return get(json, key, JSONArray.class);
    }

    public static JSONArray getJSONArray(Object jsonObjectOrArray, String key) {
        return get(jsonObjectOrArray, key, JSONArray.class);
    }


    public static <T> T get(String json, String key, Class<T> clazz) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            Object parse = JSON.parse(json);
            return get(parse, key, clazz);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        }
        return null;
    }

    public static <T> T get(Object jsonObjectOrArray, String key, Class<T> clazz) {
        if (jsonObjectOrArray == null) {
            return null;
        }
        try {
            return (T) JsonBodyParseInterceptor.parseJsonBody(jsonObjectOrArray, clazz, clazz, key);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        }
        return null;
    }


    public static <T> T get(String json, String key, TypeDef<?> typeDef) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            Object parse = JSON.parse(json);
            return get(parse, key, typeDef);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        }
        return null;
    }


    public static <T> T get(Object jsonObjectOrArray, String key, TypeDef<?> typeDef) {
        if (jsonObjectOrArray == null) {
            return null;
        }
        try {
            return (T) JsonBodyParseInterceptor.parseJsonBody(jsonObjectOrArray, typeDef.getDefClass(), typeDef.getType(), key);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        }
        return null;
    }

    public static Object getJsonObjectOrArray(String json) {
        if (StrUtil.isNotBlank(json)) {
            try {
                return JSON.parse(json);
            } catch (Exception e) {
                LogKit.error(e.toString(), e);
            }
        }
        return null;
    }


}
