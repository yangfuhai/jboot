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
package io.jboot.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import io.jboot.support.jwt.JwtManager;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class JbootController extends Controller {

    /**
     * 是否是手机浏览器
     *
     * @return
     */
    @NotAction
    public boolean isMobileBrowser() {
        return RequestUtil.isMobileBrowser(getRequest());
    }

    /**
     * 是否是微信浏览器
     *
     * @return
     */
    @NotAction
    public boolean isWechatBrowser() {
        return RequestUtil.isWechatBrowser(getRequest());
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    @NotAction
    public boolean isIEBrowser() {
        return RequestUtil.isIEBrowser(getRequest());
    }

    /**
     * 是否是ajax请求
     *
     * @return
     */
    @NotAction
    public boolean isAjaxRequest() {
        return RequestUtil.isAjaxRequest(getRequest());
    }

    /**
     * 是否是multpart的请求（带有文件上传的请求）
     *
     * @return
     */
    @NotAction
    public boolean isMultipartRequest() {
        return RequestUtil.isMultipartRequest(getRequest());
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    @NotAction
    public String getIPAddress() {
        return RequestUtil.getIpAddress(getRequest());
    }

    /**
     * 获取 referer
     *
     * @return
     */
    @NotAction
    public String getReferer() {
        return RequestUtil.getReferer(getRequest());
    }


    /**
     * 获取ua
     *
     * @return
     */
    @NotAction
    public String getUserAgent() {
        return RequestUtil.getUserAgent(getRequest());
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
            throw new NullPointerException("map is null");
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
        return RequestUtil.getBaseUrl(getRequest());

    }

    /**
     * 接收 json 转化为 object
     *
     * @param tClass
     * @param <T>
     * @return
     */
    @NotAction
    public <T> T getRawObject(Class<T> tClass) {
        return StrUtil.isBlank(getRawData()) ? null : JSON.parseObject(getRawData(), tClass);
    }


    /**
     * 接收 Json 转化为 JSONObject
     *
     * @return
     */
    @NotAction
    public JSONObject getRawObject() {
        return StrUtil.isBlank(getRawData()) ? null : JSON.parseObject(getRawData());
    }


    @Override
    @NotAction
    public String getPara(String name) {
        String value = super.getPara(name);
        return "".equals(value) ? null : value;
    }

    @NotAction
    public Map<String, String> getParas(){
        Map<String, String> map = null;
        Enumeration<String> names = getParaNames();
        if (names != null){
            map = new HashMap<>();
            while (names.hasMoreElements()){
                String name = names.nextElement();
                map.put(name,getPara(name));
            }
        }
        return map;
    }


    @NotAction
    public String getEscapePara(String name) {
        String value = super.getPara(name);
        if (value == null || "".equals(value)) {
            return null;
        }
        return StrUtil.escapeHtml(value);
    }


    @NotAction
    public String getEscapePara(String name, String defaultValue) {
        String value = super.getPara(name);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        return StrUtil.escapeHtml(value);
    }

}
