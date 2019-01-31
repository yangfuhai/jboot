package io.jboot.utils;


import io.jboot.app.config.JbootConfigManager;

public class AnnotationUtil {


    public static String get(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        } else {
            value = value.trim();
        }

        if (value.startsWith("${") && value.endsWith("}")) {
            String key = value.substring(2, value.length() - 1);
            int indexOf = key.indexOf(":");

            String defaultValue = null;
            if (indexOf != -1) {
                defaultValue = key.substring(indexOf + 1);
                key = key.substring(0, indexOf);
            }

            if (StrUtil.isBlank(key)) throw new RuntimeException("can not config empty propertie key");
            String configValue = JbootConfigManager.me().getConfigValue(key.trim());

            String returnValue = StrUtil.isBlank(configValue) ? defaultValue.trim() : configValue;
            return StrUtil.isBlank(returnValue) ? null : returnValue;
        }

        return value;
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
