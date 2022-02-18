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
package io.jboot.web.validate;

/**
 * 正则表达式大全
 * @author michael yang (fuhai999@gmail.com)
 */
public class Regex {

    /**
     * 汉字
     */
    public static final String CHINESE ="^[\\u4e00-\\u9fa5]+$";

    /**
     * 全部是英文
     */
    public static final String ENGLISH ="^[A-Za-z]+$";


    /**
     * 英文或者数字
     */
    public static final String ENGLISH_NUMBERS ="^[A-Za-z0-9]+$";


    /**
     * 英文、数字 或下划线
     */
    public static final String ENGLISH_NUMBERS_UNDERLINE ="^[A-Za-z0-9_]+$";



    /**
     * 中文、英文、数字、或下划线
     */
    public static final String CHINESE_ENGLISH_NUMBERS_UNDERLINE ="^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";

    /**
     * 正数、负数 或 小数
     */
    public static final String DECIMAL ="^(\\-|\\+)?\\d+(\\.\\d+)?$";


    /**
     * 密码长度 6~20 位数，字母、数字和下划线
     */
    public static final String CIPHER ="^[a-zA-Z0-9_]\\w{5,19}$";


    /**
     * 邮件地址
     */
    public static final String EMAIL ="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";


    /**
     * 域名
     */
    public static final String DOMAIN ="^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";

    /**
     * url 地址
     */
    public static final String URL ="[a-zA-z]+://[^\\s]*";

    /**
     * 手机号码
     */
    public static final String MOBILE ="^(1[3,4,5,6,7,8,9])\\d{9}$";

    /**
     * 电话号码
     */
    public static final String TELEPHONE ="^(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}$";

    /**
     * 身份证号码
     */
    public static final String ID_CARD ="^[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";


    /**
     * 日期格式 2020-02-0
     */
    public static final String DATE ="^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$";


    /**
     * 日期格式 2020-02-02 23:12:23 或者 2020-02-02 23:12
     */
    public static final String DATE_TIME ="^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d(:[0-5]\\d)?$";


    /**
     * 时间格式(没有秒) 2020-02-02 23:12
     */
    public static final String DATE_TIME_HM ="^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d$";



    /**
     * 时间格式 2020-02-02 23:12:23
     */
    public static final String DATE_TIME_HMS ="^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$";



    /**
     * ip地址
     */
    public static final String IP ="^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";



    public static void main(String[] args){

        System.out.println("\"汉字\".matches(CHINESE) ---> " + ("汉字".matches(CHINESE)));
        System.out.println("\"汉字123\".matches(CHINESE) ---> " + ("汉字123".matches(CHINESE)));
        System.out.println();



        System.out.println("\"abc\".matches(ENGLISH) ---> " + ("abc".matches(ENGLISH)));
        System.out.println("\"abc123\".matches(ENGLISH) ---> " + ("abc123".matches(ENGLISH)));
        System.out.println();



        System.out.println("\"abc123\".matches(ENGLISH_NUMBERS) ---> " + ("abc123".matches(ENGLISH_NUMBERS)));
        System.out.println("\"abc_123\".matches(ENGLISH_NUMBERS) ---> " + ("abc_123".matches(ENGLISH_NUMBERS)));
        System.out.println("\"汉字abc123\".matches(ENGLISH_NUMBERS) ---> " + ("汉字汉字abc123".matches(ENGLISH_NUMBERS)));
        System.out.println();



        System.out.println("\"abc123\".matches(ENGLISH_NUMBERS_UNDERLINE) ---> " + ("abc123".matches(ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println("\"abc_123\".matches(ENGLISH_NUMBERS_UNDERLINE) ---> " + ("abc_123".matches(ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println("\"汉字abc123\".matches(ENGLISH_NUMBERS_UNDERLINE) ---> " + ("汉字汉字abc123".matches(ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println();



        System.out.println("\"abc123\".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE) ---> " + ("abc123".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println("\"abc_123\".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE) ---> " + ("abc_123".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println("\"汉字abc123\".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE) ---> " + ("汉字汉字abc123".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println("\"汉字abc123#\".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE) ---> " + ("汉字abc123#".matches(CHINESE_ENGLISH_NUMBERS_UNDERLINE)));
        System.out.println();



        System.out.println("\"汉字\".matches(DECIMAL) ---> " + ("汉字".matches(DECIMAL)));
        System.out.println("\"123\".matches(DECIMAL) ---> " + ("123".matches(DECIMAL)));
        System.out.println("\"123.12\".matches(DECIMAL) ---> " + ("123.12".matches(DECIMAL)));
        System.out.println("\"-12\".matches(DECIMAL) ---> " + ("-12".matches(DECIMAL)));
        System.out.println("\"-12.12\".matches(DECIMAL) ---> " + ("-12.12".matches(DECIMAL)));
        System.out.println();



        System.out.println("\"123\".matches(PASSWORD) ---> " + ("123".matches(CIPHER)));
        System.out.println("\"123456\".matches(PASSWORD) ---> " + ("123456".matches(CIPHER)));
        System.out.println();



        System.out.println("\"汉字\".matches(EMAIL) ---> " + ("汉字".matches(EMAIL)));
        System.out.println("\"abc\".matches(EMAIL) ---> " + ("abc".matches(EMAIL)));
        System.out.println("\"abc@gmail\".matches(EMAIL) ---> " + ("bc.abc@gmail".matches(EMAIL)));
        System.out.println("\"abc.abc@gmail\".matches(EMAIL) ---> " + ("bc.abc@gmail".matches(EMAIL)));
        System.out.println("\"abc@gmail.com\".matches(EMAIL) ---> " + ("bc.abc@gmail.com".matches(EMAIL)));
        System.out.println("\"abc.abc@gmail.com\".matches(EMAIL) ---> " + ("bc.abc@gmail.com".matches(EMAIL)));
        System.out.println();



        System.out.println("\"yangfuhai\".matches(DOMAIN) ---> " + ("yangfuhai".matches(DOMAIN)));
        System.out.println("\"yangfuhai.com\".matches(DOMAIN) ---> " + ("yangfuhai.com".matches(DOMAIN)));
        System.out.println("\"www.yangfuhai.com\".matches(DOMAIN) ---> " + ("www.yangfuhai.com".matches(DOMAIN)));
        System.out.println("\"http://www.yangfuhai.com\".matches(DOMAIN) ---> " + ("http://www.yangfuhai.com".matches(DOMAIN)));
        System.out.println("\"http://www.yangfuhai.com/\".matches(DOMAIN) ---> " + ("http://www.yangfuhai.com/".matches(DOMAIN)));
        System.out.println();



        System.out.println("\"汉字\".matches(URL) ---> " + ("汉字".matches(URL)));
        System.out.println("\"abc\".matches(URL) ---> " + ("abc".matches(URL)));
        System.out.println("\"http://www\".matches(URL) ---> " + ("http://www".matches(URL)));
        System.out.println("\"http://www.yangfuhai.com\".matches(URL) ---> " + ("http://www.yangfuhai.com".matches(URL)));
        System.out.println();



        System.out.println("\"汉字\".matches(MOBILE) ---> " + ("汉字".matches(MOBILE)));
        System.out.println("\"12345\".matches(MOBILE) ---> " + ("12345".matches(MOBILE)));
        System.out.println("\"11611223344\".matches(MOBILE) ---> " + ("11611223344".matches(MOBILE)));
        System.out.println("\"12611223344\".matches(MOBILE) ---> " + ("12611223344".matches(MOBILE)));
        System.out.println("\"13611223344\".matches(MOBILE) ---> " + ("13611223344".matches(MOBILE)));
        System.out.println("\"14611223344\".matches(MOBILE) ---> " + ("14611223344".matches(MOBILE)));
        System.out.println("\"15611223344\".matches(MOBILE) ---> " + ("15611223344".matches(MOBILE)));
        System.out.println("\"16611223344\".matches(MOBILE) ---> " + ("16611223344".matches(MOBILE)));
        System.out.println("\"17611223344\".matches(MOBILE) ---> " + ("17611223344".matches(MOBILE)));
        System.out.println("\"18611223344\".matches(MOBILE) ---> " + ("18611223344".matches(MOBILE)));
        System.out.println("\"19611223344\".matches(MOBILE) ---> " + ("19611223344".matches(MOBILE)));
        System.out.println("\"196112233441\".matches(MOBILE) ---> " + ("196112233441".matches(MOBILE)));
        System.out.println();



        System.out.println("\"汉字\".matches(TELEPHONE) ---> " + ("汉字".matches(TELEPHONE)));
        System.out.println("\"021-1234567\".matches(TELEPHONE) ---> " + ("021-1234567".matches(TELEPHONE)));
        System.out.println("\"0855-1234567\".matches(TELEPHONE) ---> " + ("0855-1234567".matches(TELEPHONE)));
        System.out.println("\"1234567\".matches(TELEPHONE) ---> " + ("1234567".matches(TELEPHONE)));
        System.out.println();



        System.out.println("\"汉字\".matches(ID_CARD) ---> " + ("汉字".matches(ID_CARD)));
        System.out.println("\"522\".matches(ID_CARD) ---> " + ("522".matches(ID_CARD)));
        System.out.println("\"522000000000000000\".matches(ID_CARD) ---> " + ("522000000000000000".matches(ID_CARD)));
        System.out.println("\"52260119000125205x\".matches(ID_CARD) ---> " + ("52260119000125205x".matches(ID_CARD)));
        System.out.println();



        System.out.println("\"汉字\".matches(DATE) ---> " + ("汉字".matches(DATE)));
        System.out.println("\"abc\".matches(DATE) ---> " + ("abc".matches(DATE)));
        System.out.println("\"123\".matches(DATE) ---> " + ("123".matches(DATE)));
        System.out.println("\"2020-02-02\".matches(DATE) ---> " + ("2020-02-02".matches(DATE)));
        System.out.println("\"2020-02-02 \".matches(DATE) ---> " + ("2020-02-02 ".matches(DATE)));
        System.out.println("\"2020-02-02 23:32\".matches(DATE) ---> " + ("2020-02-02 23:32".matches(DATE)));
        System.out.println("\"2020-02-02 23:32:21\".matches(DATE) ---> " + ("2020-02-02 23:32:21".matches(DATE)));
        System.out.println();



        System.out.println("\"汉字\".matches(DATE_TIME) ---> " + ("汉字".matches(DATE_TIME)));
        System.out.println("\"abc\".matches(DATE_TIME) ---> " + ("abc".matches(DATE_TIME)));
        System.out.println("\"123\".matches(DATE_TIME) ---> " + ("123".matches(DATE_TIME)));
        System.out.println("\"2020-02-02\".matches(DATE_TIME) ---> " + ("2020-02-02".matches(DATE_TIME)));
        System.out.println("\"2020-02-02 \".matches(DATE_TIME) ---> " + ("2020-02-02 ".matches(DATE_TIME)));
        System.out.println("\"2020-02-02 23:32\".matches(DATE_TIME) ---> " + ("2020-02-02 23:32".matches(DATE_TIME)));
        System.out.println("\"2020-02-02 23:32:21\".matches(DATE_TIME) ---> " + ("2020-02-02 23:32:21".matches(DATE_TIME)));
        System.out.println();



        System.out.println("\"汉字\".matches(DATE_TIME_HM) ---> " + ("汉字".matches(DATE_TIME_HM)));
        System.out.println("\"abc\".matches(DATE_TIME_HM) ---> " + ("abc".matches(DATE_TIME_HM)));
        System.out.println("\"123\".matches(DATE_TIME_HM) ---> " + ("123".matches(DATE_TIME_HM)));
        System.out.println("\"2020-02-02\".matches(DATE_TIME_HM) ---> " + ("2020-02-02".matches(DATE_TIME_HM)));
        System.out.println("\"2020-02-02 \".matches(DATE_TIME_HM) ---> " + ("2020-02-02 ".matches(DATE_TIME_HM)));
        System.out.println("\"2020-02-02 23:32\".matches(DATE_TIME_HM) ---> " + ("2020-02-02 23:32".matches(DATE_TIME_HM)));
        System.out.println("\"2020-02-02 23:32:21\".matches(DATE_TIME_HM) ---> " + ("2020-02-02 23:32:21".matches(DATE_TIME_HM)));
        System.out.println();



        System.out.println("\"汉字\".matches(DATE_TIME_HMS) ---> " + ("汉字".matches(DATE_TIME_HMS)));
        System.out.println("\"abc\".matches(DATE_TIME_HMS) ---> " + ("abc".matches(DATE_TIME_HMS)));
        System.out.println("\"123\".matches(DATE_TIME_HMS) ---> " + ("123".matches(DATE_TIME_HMS)));
        System.out.println("\"2020-02-02\".matches(DATE_TIME_HMS) ---> " + ("2020-02-02".matches(DATE_TIME_HMS)));
        System.out.println("\"2020-02-02 \".matches(DATE_TIME_HMS) ---> " + ("2020-02-02 ".matches(DATE_TIME_HMS)));
        System.out.println("\"2020-02-02 23:32\".matches(DATE_TIME_HMS) ---> " + ("2020-02-02 23:32".matches(DATE_TIME_HMS)));
        System.out.println("\"2020-02-02 23:32:21\".matches(DATE_TIME_HMS) ---> " + ("2020-02-02 23:32:21".matches(DATE_TIME_HMS)));
        System.out.println();



        System.out.println("\"汉字\".matches(IP) ---> " + ("汉字".matches(IP)));
        System.out.println("\"abc\".matches(IP) ---> " + ("abc".matches(IP)));
        System.out.println("\"123\".matches(IP) ---> " + ("123".matches(IP)));
        System.out.println("\"123.123.123\".matches(IP) ---> " + ("123.123.123".matches(IP)));
        System.out.println("\"123.123.123.123\".matches(IP) ---> " + ("123.123.123.123".matches(IP)));
        System.out.println("\"255.255.255.255\".matches(IP) ---> " + ("255.255.255.255".matches(IP)));
        System.out.println("\"255.255.256.255\".matches(IP) ---> " + ("255.255.256.255".matches(IP)));
        System.out.println("\"0.0.0.0\".matches(IP) ---> " + ("0.0.0.0".matches(IP)));
    }



}
