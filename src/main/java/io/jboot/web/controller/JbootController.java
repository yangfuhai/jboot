/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.RenderManager;
import io.jboot.support.jwt.JwtManager;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
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


    @NotAction
    public String getCurrentUrl() {
        return RequestUtil.getCurrentUrl(getRequest());
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
        return StrUtil.isBlank(getRawData()) ? null : JsonKit.parse(getRawData(), tClass);
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
    public Map<String, String> getParas() {
        Map<String, String> map = null;
        Enumeration<String> names = getParaNames();
        if (names != null) {
            map = new HashMap<>();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, getPara(name));
            }
        }
        return map;
    }


    @NotAction
    public String getTrimPara(String name) {
        String value = super.getPara(name);
        value = (value == null ? null : value.trim());
        return "".equals(value) ? null : value;
    }


    @NotAction
    public String getTrimPara(int index) {
        String value = super.getPara(index);
        value = (value == null ? null : value.trim());
        return "".equals(value) ? null : value;
    }


    @NotAction
    public String getEscapePara(String name) {
        String value = getTrimPara(name);
        if (value == null || "".equals(value)) {
            return null;
        }
        return StrUtil.escapeHtml(value);
    }


    @NotAction
    public String getEscapePara(String name, String defaultValue) {
        String value = getTrimPara(name);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        return StrUtil.escapeHtml(value);
    }


    private BigInteger toBigInteger(String value, BigInteger defaultValue) {
        try {
            if (StrKit.isBlank(value)) {
                return defaultValue;
            }
            value = value.trim();
            if (value.startsWith("N") || value.startsWith("n")) {
                return BigInteger.ZERO.subtract(new BigInteger(value));
            }
            return new BigInteger(value);
        } catch (Exception e) {
            throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), "Can not parse the parameter \"" + value + "\" to BigInteger value.");
        }
    }

    /**
     * Returns the value of a request parameter and convert to BigInteger.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigInteger getParaToBigInteger(String name) {
        return toBigInteger(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigInteger with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigInteger getParaToBigInteger(String name, BigInteger defaultValue) {
        return toBigInteger(getTrimPara(name), defaultValue);
    }


    /**
     * Returns the value of a request parameter and convert to BigInteger.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigInteger getBigInteger(String name) {
        return toBigInteger(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigInteger with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigInteger getBigInteger(String name, BigInteger defaultValue) {
        return toBigInteger(getTrimPara(name), defaultValue);
    }


    private BigDecimal toBigDecimal(String value, BigDecimal defaultValue) {
        try {
            if (StrKit.isBlank(value)) {
                return defaultValue;
            }
            value = value.trim();
            if (value.startsWith("N") || value.startsWith("n")) {
                return BigDecimal.ZERO.subtract(new BigDecimal(value));
            }
            return new BigDecimal(value);
        } catch (Exception e) {
            throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), "Can not parse the parameter \"" + value + "\" to BigDecimal value.");
        }
    }

    /**
     * Returns the value of a request parameter and convert to BigDecimal.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getParaToBigDecimal(String name) {
        return toBigDecimal(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigDecimal with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getParaToBigDecimal(String name, BigDecimal defaultValue) {
        return toBigDecimal(getTrimPara(name), defaultValue);
    }


    /**
     * Returns the value of a request parameter and convert to BigDecimal.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getBigDecimal(String name) {
        return toBigDecimal(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigDecimal with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a Integer representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        return toBigDecimal(getTrimPara(name), defaultValue);
    }

}
