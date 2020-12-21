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
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.RenderManager;
import io.jboot.exception.JbootException;
import io.jboot.support.jwt.JwtManager;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.utils.TypeDef;
import io.jboot.web.json.JsonBodyParseInterceptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class JbootController extends Controller {

    private Object rawObject;
    private Map jwtParas;

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


    private Map<String, Object> jwtAttrs;

    @NotAction
    public Controller setJwtAttr(String name, Object value) {
        if (jwtAttrs == null) {
            jwtAttrs = new HashMap<>();
        }

        jwtAttrs.put(name, value);
        return this;
    }


    @NotAction
    public Controller setJwtMap(Map map) {
        if (map == null) {
            throw new NullPointerException("Jwt attrs is null");
        }
        if (jwtAttrs == null) {
            jwtAttrs = new HashMap<>();
        }

        jwtAttrs.putAll(map);
        return this;
    }


    @NotAction
    public <T> T getJwtAttr(String name) {
        return jwtAttrs == null ? null : (T) jwtAttrs.get(name);
    }


    @NotAction
    public Map<String, Object> getJwtAttrs() {
        return jwtAttrs;
    }


    @NotAction
    public <T> T getJwtPara(String name) {
        if (jwtParas == null) {
            synchronized (this) {
                if (jwtParas == null) {
                    initJwtParas();
                }
            }
        }
        return (T) jwtParas.get(name);
    }



    @NotAction
    public Map getJwtParas() {
        if (jwtParas == null) {
            synchronized (this) {
                if (jwtParas == null) {
                    initJwtParas();
                }
            }
        }
        return jwtParas;
    }

    private void initJwtParas() {
        if (!JwtManager.me().getConfig().isConfigOk()) {
            throw new JbootException("Jwt secret not config well, please config jboot.web.jwt.secret in jboot.properties.");
        }

        String token = getHeader(JwtManager.me().getHttpHeaderName());

        if (StrUtil.isBlank(token) && StrUtil.isNotBlank(JwtManager.me().getHttpParameterKey())) {
            token = getPara(JwtManager.me().getHttpParameterKey());
        }

        if (StrUtil.isBlank(token)) {
            LogKit.error("Can not get jwt token form http header or parameter!!");
            jwtParas = JwtManager.EMPTY_MAP;
        } else {
            jwtParas = JwtManager.me().parseJwtToken(token);
        }
    }

    @NotAction
    public String createJwtToken() {
        if (jwtAttrs == null) {
            throw new NullPointerException("jwt attrs is null");
        }
        return JwtManager.me().createJwtToken(jwtAttrs);
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
     * 接收 Json 转化为 JsonObject 或者 JsonArray
     *
     * @return
     */
    @NotAction
    public <T> T getRawObject() {
        if (rawObject == null && StrUtil.isNotBlank(getRawData())) {
            rawObject = JSON.parse(getRawData());
        }
        return (T) rawObject;
    }


    /**
     * 接收 json 转化为 object
     *
     * @param typeClass
     * @param <T>
     * @return
     */
    @NotAction
    public <T> T getRawObject(Class<T> typeClass) {
        return getRawObject(typeClass, null);
    }


    /**
     * 接收 json 转化为 object
     *
     * @param typeClass
     * @param jsonKey
     * @param <T>
     * @return
     */
    @NotAction
    public <T> T getRawObject(Class<T> typeClass, String jsonKey) {
        try {
            return (T) JsonBodyParseInterceptor.parseJsonBody(getRawObject(), typeClass, typeClass, jsonKey);
        } catch (Exception ex) {
            throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), ex.getMessage());
        }
    }


    /**
     * 接收 json 转化为 object
     *
     * @param typeDef 泛型的定义类
     * @param <T>
     * @return
     */
    @NotAction
    public <T> T getRawObject(TypeDef<T> typeDef) {
        return getRawObject(typeDef, null);
    }


    /**
     * 接收 json 转化为 object
     *
     * @param typeDef 泛型的定义类
     * @param jsonKey
     * @param <T>
     * @return
     */
    @NotAction
    public <T> T getRawObject(TypeDef<T> typeDef, String jsonKey) {
        try {
            return (T) JsonBodyParseInterceptor.parseJsonBody(getRawObject(), typeDef.getDefClass(), typeDef.getType(), jsonKey);
        } catch (Exception ex) {
            throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400), ex.getMessage());
        }
    }


    /**
     * BeanGetter 会调用此方法生成 bean，在 Map List Array 下，JFinal
     * 通过 Injector.injectBean 去实例化的时候会出错，从而无法实现通过 @JsonBody 对 map list array 的注入
     *
     * @param beanClass
     * @param beanName
     * @param skipConvertError
     * @param <T>
     * @return
     */
    @NotAction
    @Override
    public <T> T getBean(Class<T> beanClass, String beanName, boolean skipConvertError) {
        if (Collection.class.isAssignableFrom(beanClass)
                || Map.class.isAssignableFrom(beanClass)
                || beanClass.isArray()) {
            return null;
        } else {
            return super.getBean(beanClass, beanName, skipConvertError);
        }
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
     * @return a BigInteger representing the single value of the parameter
     */
    @NotAction
    public BigInteger getParaToBigInteger(String name) {
        return toBigInteger(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigInteger with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigInteger representing the single value of the parameter
     */
    @NotAction
    public BigInteger getParaToBigInteger(String name, BigInteger defaultValue) {
        return toBigInteger(getTrimPara(name), defaultValue);
    }


    /**
     * Returns the value of a request parameter and convert to BigInteger.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigInteger representing the single value of the parameter
     */
    @NotAction
    public BigInteger getBigInteger(String name) {
        return toBigInteger(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigInteger with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigInteger representing the single value of the parameter
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
     * @return a BigDecimal representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getParaToBigDecimal(String name) {
        return toBigDecimal(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigDecimal with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigDecimal representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getParaToBigDecimal(String name, BigDecimal defaultValue) {
        return toBigDecimal(getTrimPara(name), defaultValue);
    }


    /**
     * Returns the value of a request parameter and convert to BigDecimal.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigDecimal representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getBigDecimal(String name) {
        return toBigDecimal(getTrimPara(name), null);
    }

    /**
     * Returns the value of a request parameter and convert to BigDecimal with a default value if it is null.
     *
     * @param name a String specifying the name of the parameter
     * @return a BigDecimal representing the single value of the parameter
     */
    @NotAction
    public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        return toBigDecimal(getTrimPara(name), defaultValue);
    }
}
