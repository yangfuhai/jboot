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

import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.web.validate.Regex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil extends StrKit {

    private static final Log log = Log.getLog(StrUtil.class);

    public static final String EMPTY = "";
    public static final String SPACE = " ";

    public static String urlDecode(String string) {
        try {
            return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode is error", e);
        }
        return string;
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncode is error", e);
        }
        return string;
    }

    public static String urlRedirect(String redirect) {
        try {
            redirect = new String(redirect.getBytes(JFinal.me().getConstants().getEncoding()), "ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            log.error("urlRedirect is error", e);
        }
        return redirect;
    }

    private static final Map<String, String> EMPTY_MAP = new HashMap<>();

    public static Map<String, String> queryStringToMap(String queryString) {
        if (StrUtil.isBlank(queryString)) {
            return EMPTY_MAP;
        }

        Map<String, String> map = new HashMap<>();
        String[] params = queryString.split("&");
        for (String paramPair : params) {
            String[] keyAndValue = paramPair.split("=");
            if (keyAndValue.length == 2) {
                map.put(keyAndValue[0], keyAndValue[1]);
            } else if (keyAndValue.length == 1) {
                map.put(keyAndValue[0], "");
            }
        }
        return map;
    }


    public static String mapToQueryString(Map map) {
        if (map == null || map.isEmpty()) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        for (Object key : map.keySet()) {
            if (key == null || key.equals(StrUtil.EMPTY)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(key.toString().trim());
            sb.append("=");
            Object value = map.get(key);
            sb.append(value == null ? EMPTY : StrUtil.urlEncode(value.toString().trim()));
        }


        return sb.toString();
    }

    public static boolean areNotEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }

        for (String string : strs) {
            if (string == null || EMPTY.equals(string)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnyBlank(String... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }

        for (String str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStartsWithAny(String str, String... prefixes) {
        if (isBlank(str) || prefixes == null || prefixes.length == 0) {
            return false;
        }

        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }


    public static String requireNonBlank(String str) {
        if (isBlank(str)) {
            throw new NullPointerException();
        }
        return str;
    }

    public static String requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new NullPointerException(message);
        }
        return str;
    }

    @Deprecated
    public static String obtainDefaultIfBlank(String value, String defaultValue) {
        return obtainDefault(value, defaultValue);
    }


    public static String obtainDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }


    public static String obtainNotBlank(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Arguments is null or empty.");
        }

        for (String value : values) {
            if (isNotBlank(value)) {
                return value;
            }
        }

        return null;
    }

    /**
     * 不是空数据，注意：空格不是空数据
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.equals(EMPTY);
    }


    /**
     * 确保不是空白字符串
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(Object str) {
        return str != null && notBlank(str.toString());
    }


    /**
     * null 或者 空内容字符串
     * 使用 isBlank 代替
     *
     * @param str
     * @return
     */
    @Deprecated
    public static boolean isNullOrBlank(String str) {
        return isBlank(str);
    }


    /**
     * 字符串是否匹配某个正则
     *
     * @param string
     * @param regex
     * @return
     */
    public static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    /**
     * 这个字符串是否是全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 这个字符串是否是可能包含小数点的数字
     *
     * @param str
     * @return
     */
    public static boolean isDecimal(String str) {
        if (isBlank(str)) {
            return false;
        }
        boolean hasDot = false;
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if ((chr < 48 || chr > 57) && chr != '.') {
                return false;
            }
            if (chr == '.') {
                if (hasDot) {
                    return false;
                } else {
                    hasDot = true;
                }
            }
        }
        return true;
    }

    /**
     * 是否是邮件的字符串
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return isNotBlank(email) && email.matches(Regex.EMAIL);
    }


    /**
     * 是否是中国地区手机号码
     *
     * @param mobileNumber
     * @return
     */
    public static boolean isMobileNumber(String mobileNumber) {
        return isNotBlank(mobileNumber) && mobileNumber.matches(Regex.MOBILE);
    }


    /**
     * 生成一个新的UUID
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 根据逗号分隔为set
     *
     * @param src
     * @return
     */
    public static Set<String> splitToSetByComma(String src) {
        return splitToSet(src, ",");
    }

    /**
     * 把字符串拆分成一个set
     *
     * @param src
     * @param regex
     * @return
     */
    public static Set<String> splitToSet(String src, String regex) {
        if (src == null) {
            return null;
        }

        String[] strings = src.split(regex);
        Set<String> set = new LinkedHashSet<>();
        for (String s : strings) {
            if (StrUtil.isBlank(s)) {
                continue;
            }
            set.add(s.trim());
        }
        return set;
    }


    private static final String[] htmlChars = {"&", "<", ">", "'", "\""};
    private static final String[] escapeChars = {"&amp;", "&lt;", "&gt;", "&#39;", "&quot;"};

    public static String escapeHtml(String content) {
        return isBlank(content) ? content : StringUtils.replaceEach(content, htmlChars, escapeChars);
    }

    public static String unEscapeHtml(String content) {
        return isBlank(content) ? content : StringUtils.replaceEach(content, escapeChars, htmlChars);
    }

    public static Model escapeModel(Model model, String... ignoreAttrs) {
        String[] attrNames = model._getAttrNames();
        for (String attr : attrNames) {

            if (ArrayUtils.contains(ignoreAttrs, attr)) {
                continue;
            }

            Object value = model.get(attr);

            if (value instanceof String) {
                model.set(attr, escapeHtml(value.toString()));
            }
        }

        return model;
    }

    public static Map escapeMap(Map map, Object... ignoreKeys) {
        if (map == null || map.isEmpty()) {
            return map;
        }

        Set<?> keys = map.keySet();
        for (Object key : keys) {
            if (ArrayUtils.contains(ignoreKeys, key)) {
                continue;
            }

            Object value = map.get(key);

            if (value instanceof String) {
                map.put(key, escapeHtml(value.toString()));
            }
        }

        return map;
    }


    public static String join(String[] array, String split) {
        if (array == null || array.length == 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(split);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }


    public static String join(Collection<String> coll, String split) {
        if (coll == null || coll.isEmpty()) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : coll) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(split);
            }
            sb.append(s);
        }
        return sb.toString();
    }

}
