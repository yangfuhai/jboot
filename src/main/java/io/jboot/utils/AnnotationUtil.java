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
