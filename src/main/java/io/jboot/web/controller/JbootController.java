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

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import io.jboot.component.jwt.JwtManager;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StrUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class JbootController extends Controller {

    /**
     * 是否是手机浏览器
     *
     * @return
     */
    @NotAction
    public boolean isMoblieBrowser() {
        return RequestUtils.isMoblieBrowser(getRequest());
    }

    /**
     * 是否是微信浏览器
     *
     * @return
     */
    @NotAction
    public boolean isWechatBrowser() {
        return RequestUtils.isWechatBrowser(getRequest());
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    @NotAction
    public boolean isIEBrowser() {
        return RequestUtils.isIEBrowser(getRequest());
    }

    /**
     * 是否是ajax请求
     *
     * @return
     */
    @NotAction
    public boolean isAjaxRequest() {
        return RequestUtils.isAjaxRequest(getRequest());
    }

    /**
     * 是否是multpart的请求（带有文件上传的请求）
     *
     * @return
     */
    @NotAction
    public boolean isMultipartRequest() {
        return RequestUtils.isMultipartRequest(getRequest());
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    @NotAction
    public String getIPAddress() {
        return RequestUtils.getIpAddress(getRequest());
    }

    /**
     * 获取 referer
     *
     * @return
     */
    @NotAction
    public String getReferer() {
        return RequestUtils.getReferer(getRequest());
    }


    /**
     * 获取ua
     *
     * @return
     */
    @NotAction
    public String getUserAgent() {
        return RequestUtils.getUserAgent(getRequest());
    }


    protected HashMap<String, Object> flash;

    @NotAction
    public Controller setFlashAttr(String name, Object value) {
        if (flash == null) {
            flash = new HashMap<>();
        }

        flash.put(name, value);
        return this;
    }


    @NotAction
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


    @NotAction
    public <T> T getFlashAttr(String name) {
        return flash == null ? null : (T) flash.get(name);
    }


    @NotAction
    public HashMap<String, Object> getFlashAttrs() {
        return flash;
    }


    private HashMap<String, Object> jwtMap;

    @NotAction
    public Controller setJwtAttr(String name, Object value) {
        if (jwtMap == null) {
            jwtMap = new HashMap<>();
        }

        jwtMap.put(name, value);
        return this;
    }


    @NotAction
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


    @NotAction
    public <T> T getJwtAttr(String name) {
        return jwtMap == null ? null : (T) jwtMap.get(name);
    }


    @NotAction
    public HashMap<String, Object> getJwtAttrs() {
        return jwtMap;
    }

    @NotAction
    public <T> T getJwtPara(String name) {
        return JwtManager.me().getPara(name);
    }

    @NotAction
    public Map getJwtParas() {
        return JwtManager.me().getParas();
    }

    @NotAction
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
    @NotAction
    public String getBaseUrl() {
        HttpServletRequest req = getRequest();
        int port = req.getServerPort();

        return port == 80
                ? String.format("%s://%s%s", req.getScheme(), req.getServerName(), req.getContextPath())
                : String.format("%s://%s%s%s", req.getScheme(), req.getServerName(), ":" + port, req.getContextPath());

    }

    @NotAction
    public <T> T getRawObject(Class<T> tClass) {
        return StrUtils.isBlank(getRawData()) ? null : JSON.parseObject(getRawData(), tClass);
    }


}
