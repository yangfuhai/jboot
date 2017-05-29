/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.utils;

import com.jfinal.core.JFinal;
import com.jfinal.log.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Log log = Log.getLog(StringUtils.class);

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

    public static boolean isNotEmpty(String string) {
        return string != null && !string.equals("");
    }

    public static boolean areNotBlank(String... strings) {
        if (strings == null || strings.length == 0)
            return false;

        for (String string : strings) {
            if (string == null || "".equals(string.trim())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(Object o) {
        return o == null ? false : isNotBlank(o.toString());
    }

    public static boolean isNotBlank(String string) {
        return string != null && !string.trim().equals("");
    }

    public static boolean isBlank(String string) {
        return string == null || string.trim().equals("");
    }

    public static long toLong(String value, Long defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            value = value.trim();
            if (value.startsWith("N") || value.startsWith("n"))
                return -Long.parseLong(value.substring(1));
            return Long.parseLong(value);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static int toInt(String value, int defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            value = value.trim();
            if (value.startsWith("N") || value.startsWith("n"))
                return -Integer.parseInt(value.substring(1));
            return Integer.parseInt(value);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static BigInteger toBigInteger(String value, BigInteger defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            value = value.trim();
            if (value.startsWith("N") || value.startsWith("n"))
                return new BigInteger(value).negate();
            return new BigInteger(value);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

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

    public static boolean isEmail(String email) {
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }

    public static boolean isMobileNumber(String phoneNumber) {
        return Pattern.matches("^(1[3,4,5,7,8])\\d{9}$", phoneNumber);
    }

    public static String escapeHtml(String text) {
        if (isBlank(text))
            return text;

        return text.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;").replace("/", "&#x2F;");
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成流水号
     *
     * @param uuid 谋订单的主键ID
     * @return
     */
    public static String generateSerialNumber(String uuid) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + Math.abs(uuid.hashCode());
    }

    public static String clearSpecialCharacter(String string) {
        if (isBlank(string)) {
            return string;
        }

        /**
         P：标点字符；
         L：字母；
         M：标记符号（一般不会单独出现）；
         Z：分隔符（比如空格、换行等）；
         S：符号（比如数学符号、货币符号等）；
         N：数字（比如阿拉伯数字、罗马数字等）；
         C：其他字符
         */
//        return string.replaceAll("[\\pP\\pZ\\pM\\pC]", "");
        return string.replaceAll("[\\\\\'\"\\/\f\n\r\t]", "");
    }


    /**
     * 生成验证码
     */
    public static String getValidateCode() {

        Random random = new Random();
        return String.valueOf(random.nextInt(9999 - 1000 + 1) + 1000);//为变量赋随机值1000-9999
    }


    public static void main(String[] args) {
        String url = "http://www.baidu.com?username=aaa";

        System.out.println(StringUtils.urlEncode(url));
    }
}
