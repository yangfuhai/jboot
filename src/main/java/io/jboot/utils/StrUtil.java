/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil extends StrKit {

    private static final Log log = Log.getLog(StrUtil.class);

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

    public static boolean areNotEmpty(String... strings) {
        if (strings == null || strings.length == 0)
            return false;

        for (String string : strings) {
            if (string == null || "".equals(string)) {
                return false;
            }
        }
        return true;
    }

    public static String requireNonBlank(String string) {
        if (isBlank(string))
            throw new NullPointerException();
        return string;
    }

    public static String requireNonBlank(String string, String message) {
        if (isBlank(string))
            throw new NullPointerException(message);
        return string;
    }

    public static String obtainDefaultIfBlank(String string, String defaultValue) {
        return isBlank(string) ? defaultValue : string;
    }

    /**
     * 不是空数据，注意：空格不是空数据
     *
     * @param string
     * @return
     */
    public static boolean isNotEmpty(String string) {
        return string != null && !string.equals("");
    }


    /**
     * 确保不是空白字符串
     *
     * @param o
     * @return
     */
    public static boolean isNotBlank(Object o) {
        return o == null ? false : notBlank(o.toString());
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
        if (str == null)
            return false;
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
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
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }


    /**
     * 是否是中国地区手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumber(String phoneNumber) {
        return Pattern.matches("^(1[3,4,5,7,8,9])\\d{9}$", phoneNumber);
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
        Set<String> set = new HashSet<>();
        for (String table : strings) {
            if (StrUtil.isBlank(table)) {
                continue;
            }
            set.add(table.trim());
        }
        return set;
    }


    public static String escapeHtml(String content) {

        if (isBlank(content)) {
            return content;
        }

        /**
         "&lt;" represents the < sign.
         "&gt;" represents the > sign.
         "&amp;" represents the & sign.
         "&quot; represents the " mark.
         */

        return unEscapeHtml(content)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&#39;")
                .replace("\"", "&quot;");
    }


    public static String unEscapeHtml(String content) {

        if (isBlank(content)) {
            return content;
        }

        return content
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&#39;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&");
    }


    public static void main(String[] args) {
        String url = "http://www.baidu.com?username=aaa";
        System.out.println(StrUtil.urlEncode(url));
    }
}
