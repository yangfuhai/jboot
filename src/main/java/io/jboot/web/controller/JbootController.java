/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.HttpKit;
import io.jboot.component.jwt.JwtManager;
import io.jboot.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class JbootController extends Controller {

    private static final Object NULL_OBJ = new Object();
    private static final String BODY_STRING_ATTR = "__body_str";

    /**
     * 是否是手机浏览器
     *
     * @return
     */
    @Before(NotAction.class)
    public boolean isMoblieBrowser() {
        return RequestUtils.isMoblieBrowser(getRequest());
    }

    /**
     * 是否是微信浏览器
     *
     * @return
     */
    @Before(NotAction.class)
    public boolean isWechatBrowser() {
        return RequestUtils.isWechatBrowser(getRequest());
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    @Before(NotAction.class)
    public boolean isIEBrowser() {
        return RequestUtils.isIEBrowser(getRequest());
    }

    /**
     * 是否是ajax请求
     *
     * @return
     */
    @Before(NotAction.class)
    public boolean isAjaxRequest() {
        return RequestUtils.isAjaxRequest(getRequest());
    }

    /**
     * 是否是multpart的请求（带有文件上传的请求）
     *
     * @return
     */
    @Before(NotAction.class)
    public boolean isMultipartRequest() {
        return RequestUtils.isMultipartRequest(getRequest());
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    @Before(NotAction.class)
    public String getIPAddress() {
        return RequestUtils.getIpAddress(getRequest());
    }

    /**
     * 获取 referer
     *
     * @return
     */
    @Before(NotAction.class)
    public String getReferer() {
        return RequestUtils.getReferer(getRequest());
    }


    /**
     * 获取ua
     *
     * @return
     */
    @Before(NotAction.class)
    public String getUserAgent() {
        return RequestUtils.getUserAgent(getRequest());
    }


    protected HashMap<String, Object> flash;

    @Before(NotAction.class)
    public Controller setFlashAttr(String name, Object value) {
        if (flash == null) {
            flash = new HashMap<>();
        }

        flash.put(name, value);
        return this;
    }


    @Before(NotAction.class)
    public Controller setFlashMap(Map map) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        if (flash == null) {
            flash = new HashMap<>();
        }

        flash.putAll(map);
        return this;
    }


    @Before(NotAction.class)
    public <T> T getFlashAttr(String name) {
        return flash == null ? null : (T) flash.get(name);
    }


    @Before(NotAction.class)
    public HashMap<String, Object> getFlashAttrs() {
        return flash;
    }


    private HashMap<String, Object> jwtMap;

    @Before(NotAction.class)
    public Controller setJwtAttr(String name, Object value) {
        if (jwtMap == null) {
            jwtMap = new HashMap<>();
        }

        jwtMap.put(name, value);
        return this;
    }


    @Before(NotAction.class)
    public Controller setJwtMap(Map map) {
        if (map == null) {
            throw new NullPointerException("map is null, u show invoke setJwtAttr() before. ");
        }
        if (jwtMap == null) {
            jwtMap = new HashMap<>();
        }

        jwtMap.putAll(map);
        return this;
    }


    @Before(NotAction.class)
    public <T> T getJwtAttr(String name) {
        return jwtMap == null ? null : (T) jwtMap.get(name);
    }


    @Before(NotAction.class)
    public HashMap<String, Object> getJwtAttrs() {
        return jwtMap;
    }

    @Before(NotAction.class)
    public <T> T getJwtPara(String name) {
        return JwtManager.me().getPara(name);
    }

    @Before(NotAction.class)
    public Map getJwtParas() {
        return JwtManager.me().getParas();
    }

    @Before(NotAction.class)
    public String createJwtToken() {
        if (jwtMap == null) {
            throw new NullPointerException("jwt attrs is null");
        }
        return JwtManager.me().createJwtToken(jwtMap);
    }

    /**
     * 获取当前网址
     *
     * @return
     */
    @Before(NotAction.class)
    public String getBaseUrl() {
        HttpServletRequest req = getRequest();
        int port = req.getServerPort();

        return port == 80
                ? String.format("%s://%s%s", req.getScheme(), req.getServerName(), req.getContextPath())
                : String.format("%s://%s%s%s", req.getScheme(), req.getServerName(), ":" + port, req.getContextPath());

    }

    @Before(NotAction.class)
    public String getBodyString() {
        Object object = getAttr(BODY_STRING_ATTR);
        if (object == NULL_OBJ) {
            return null;
        }

        if (object != null) {
            return (String) object;
        }

        object = HttpKit.readData(getRequest());
        if (object == null) {
            setAttr(BODY_STRING_ATTR, NULL_OBJ);
        } else {
            setAttr(BODY_STRING_ATTR, object);
        }

        return (String) object;
    }


}
