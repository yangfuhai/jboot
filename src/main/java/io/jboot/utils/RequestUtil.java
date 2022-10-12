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

import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootControllerContext;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    static String[] mobileAgents = {"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
            "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod", "nokia",
            "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo",
            "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith",
            "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem", "wellcom", "bunjalloo",
            "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm",
            "jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav",
            "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
            "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g",
            "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
            "oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-",
            "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo",
            "teli", "tim-", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc",
            "winw", "winw", "xda", "xda-", "googlebot-mobile"};

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    public static boolean isJsonContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }


    public static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("multipart");
    }

    /**
     * 是否是手机浏览器
     *
     * @return
     */
    public static boolean isMobileBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(ua)) {
            ua = ua.toLowerCase();
            for (String mobileAgent : mobileAgents) {
                if (ua.indexOf(mobileAgent) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是微信浏览器
     *
     * @return
     */
    public static boolean isWechatBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return StrUtil.isNotBlank(ua) && ua.toLowerCase().indexOf("micromessenger") > -1;
    }


    /**
     * 是否是PC版的微信浏览器
     *
     * @param request
     * @return
     */
    public static boolean isWechatPcBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return StrUtil.isNotBlank(ua) && ua.toLowerCase().indexOf("windowswechat") > -1;
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    public static boolean isIEBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtil.isBlank(ua)) {
            return false;
        }

        ua = ua.toLowerCase();
        if (ua.indexOf("msie") > -1) {
            return true;
        }

        if (ua.indexOf("gecko") > -1 && ua.indexOf("rv:11") > -1) {
            return true;
        }

        return false;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }


    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }


    public static String getBaseUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        return request.getScheme() + "://" +
                request.getServerName() +
                (port == 80 || port == 443 ? "" : ":" + port) +
                request.getContextPath();
    }


    public static String getBaseUrl() {
        Controller controller = JbootControllerContext.get();
        return controller == null ? null : getBaseUrl(controller.getRequest());
    }


    public static String getCurrentUrl() {
        Controller controller = JbootControllerContext.get();
        return controller == null ? null : getCurrentUrl(controller.getRequest());
    }


    public static String getCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String url = getBaseUrl(request) + request.getServletPath();
        if (StrUtil.isNotBlank(queryString)) {
            url = url.concat("?").concat(queryString);
        }
        return url;
    }


    public static String getCurrentEncodeUrl() {
        Controller controller = JbootControllerContext.get();
        return controller == null ? null : getCurrentEncodeUrl(controller.getRequest());
    }


    public static String getCurrentEncodeUrl(HttpServletRequest request) {
        return StrUtil.urlEncode(getCurrentUrl(request));
    }


}
