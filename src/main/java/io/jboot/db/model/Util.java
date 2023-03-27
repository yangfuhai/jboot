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


import com.jfinal.ext.kit.DateKit;
import io.jboot.utils.CollectionUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

class Util {

    static final Object[] NULL_PARA_ARRAY = new Object[0];

    static Object[] getValueArray(List<Column> columns) {

        if (columns == null || columns.isEmpty()) {
            return NULL_PARA_ARRAY;
        }

        List<Object> paras = new LinkedList<>();

        for (Column column : columns) {
            if (!column.hasPara()) {
                continue;
            }
            Object value = column.getValue();
            if (value != null) {
                if (value.getClass().isArray()) {
                    Object[] values = (Object[]) value;
                    for (Object v : values) {
                        if (v != null && (
                                v.getClass() == int[].class
                                        || v.getClass() == long[].class
                                        || v.getClass() == short[].class
                                        || v.getClass() == float[].class
                                        || v.getClass() == double[].class
                        )) {
                            for (int i = 0; i < Array.getLength(v); i++) {
                                paras.add(Array.get(v, i));
                            }
                        } else {
                            paras.add(v);
                        }
                    }
                } else {
                    paras.add(value);
                }
            }
        }

        return paras.isEmpty() ? NULL_PARA_ARRAY : paras.toArray();
    }



    static String replaceSqlPara(String sql, Object value) {
        // null
        if (value == null) {
            return sql.replaceFirst("\\?", "null");
        }
        // number
        else if (value instanceof Number) {
            return sql.replaceFirst("\\?", value.toString());
        }
        // numeric
        else if (value instanceof String && StrUtil.isNumeric((String) value)) {
            return sql.replaceFirst("\\?", (String) value);
        }
        // other
        else {
            StringBuilder sb = new StringBuilder();
            if (value instanceof Date) {
                sb.append(DateKit.toStr((Date) value, DateKit.timeStampPattern));
            } else {
                sb.append(value);
            }
            return sql.replaceFirst("\\?", Matcher.quoteReplacement(sb.toString()));
        }
    }


    static String deleteWhitespace(String str) {
        final int strLen = str.length();
        final char[] chs = new char[strLen];
        int count = 0;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == strLen) {
            return str;
        }
        return new String(chs, 0, count);
    }


    static String array2String(Object[] a) {
        if (a == null) {
            return "null";
        }

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append("-");
        }
    }


    static void checkNullParas(Columns columns, String name, Object... paras) {
        if (columns.isUseSafeMode()) {
            for (Object obj : paras) {
                if (obj == null) {
                    throw new NullPointerException("Column \"" + name + "\" para is null, Columns must has not null para value in safeMode.");
                }
            }
        }
    }

    static void checkNullParas(Columns columns, Object... paras) {
        if (columns.isUseSafeMode()) {
            for (Object obj : paras) {
                if (obj == null) {
                    throw new NullPointerException("Columns must has not null para value in safeMode.");
                }
            }
        }
    }

    static void checkNullParas(Columns columns, Collection<?> collection) {
        if (columns.isUseSafeMode()) {
            if (CollectionUtil.isEmpty(collection)) {
                throw new NullPointerException("Columns must has not empty collection in safeMode.");
            }
        }
    }
}
