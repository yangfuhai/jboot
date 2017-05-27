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
package io.jboot.wechat.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import io.jboot.Jboot;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.wechat.JbootWechatConfig;
import io.jboot.wechat.WechatApis;
import io.jboot.wechat.interceptor.WechatApiConfigInterceptor;
import io.jboot.wechat.interceptor.WechatUserInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Before({WechatApiConfigInterceptor.class, WechatUserInterceptor.class})
public abstract class JbootWechatController extends JbootController {


    public ApiConfig getApiConfig() {
        return ApiConfigKit.getApiConfig();
    }


    @Clear
    public void wechatCallback() {

        String gotoUrl = getPara("goto");
        String code = getPara("code");

        //获得不到code？
        if (StringUtils.isBlank(code)) {
            renderText("获取不到正确的code信息");
            return;
        }


        String appId = PropKit.get("wechat.appid");
        String appSecret = PropKit.get("wechat.appsecret");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            renderText("wechat.appid 或 wechat.appsecret配置错误");
            return;
        }


        /**
         * 在某些情况下，相同的callback会执行两次，code相同。
         */
        String wechatOpenId = getSessionAttr("WECHAT_OPEN_ID");
        String access_token = getSessionAttr("WECHAT_ACCESS_TOKEN");

        if (StringUtils.isNotBlank(wechatOpenId) && StringUtils.isNotBlank(access_token)) {
            doRedirect(gotoUrl, wechatOpenId, access_token);
            return;
        }


        ApiResult result = WechatApis.getAccessTokenAndOpenId(code);
        if (result == null) {
            renderText("网络错误，获取不到微信信息，请联系管理员");
            return;
        }


        if (!result.isSucceed()) {
            //微信在某些情况下，会执行callback两次，返回两次相同的code，导致result不成功（备注：code只能用一次）
            //在不成功的情况下，有可能是因为已经执行过一次了，已经获得了 WECHAT_OPEN_ID
            wechatOpenId = getSessionAttr("WECHAT_OPEN_ID");
            access_token = getSessionAttr("WECHAT_ACCESS_TOKEN");
            if (StringUtils.isBlank(wechatOpenId) || StringUtils.isBlank(access_token)) {
                renderText("错误：" + result.getErrorMsg());
                return;
            }
        }
        /**
         * 成功获取openId
         */
        else {
            wechatOpenId = result.getStr("openid");
            access_token = result.getStr("access_token");
            setSessionAttr("WECHAT_OPEN_ID", wechatOpenId);
            setSessionAttr("WECHAT_ACCESS_TOKEN", access_token);
            setSessionAttr("WECHAT_SCOPE", result.getStr("scope"));
        }

        if ("snsapi_base".equalsIgnoreCase(result.getStr("scope"))) {
            redirect(gotoUrl);
            return;
        }

        doRedirect(gotoUrl, wechatOpenId, access_token);
    }

    private void doRedirect(String gotoUrl, String wechatOpenId, String access_token) {

        /**
         * 由于 wechatOpenId 或者 access_token 是可能从session读取的，
         * 从而导致失效等问题
         */
        ApiResult apiResult = WechatApis.getUserInfo(access_token, wechatOpenId);

        if (!apiResult.isSucceed()) {
            redirect(gotoUrl);
            return;
        }

        setSessionAttr("WECHAT_USER_JSON", apiResult.getJson());
        redirect(gotoUrl);
    }


    @Before(NotAction.class)
    public void initJsSdkConfig() {

        JbootWechatConfig config = Jboot.config(JbootWechatConfig.class);


        // 1.拼接url（当前网页的URL，不包含#及其后面部分）
        String url = getRequest().getRequestURL().toString().split("#")[0];
        String query = getRequest().getQueryString();
        if (StringUtils.isNotBlank(query)) {
            url = url.concat("?").concat(query);
        }


        JsTicket jsTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
        String _wxJsApiTicket = jsTicket.getTicket();

        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = (System.currentTimeMillis() / 1000) + "";

        Map<String, String> _wxMap = new TreeMap<String, String>();
        _wxMap.put("noncestr", noncestr);
        _wxMap.put("timestamp", timestamp);
        _wxMap.put("jsapi_ticket", _wxJsApiTicket);
        _wxMap.put("url", url);

        //拼接字符串
        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, String> param : _wxMap.entrySet()) {
            paramsBuilder.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        String signString = paramsBuilder.substring(0, paramsBuilder.length() - 1);

        //签名
        String signature = HashKit.sha1(signString);

        setAttr("wechatDebug", config.getDebug());
        setAttr("wechatAppId", getApiConfig().getAppId());
        setAttr("wechatNoncestr", noncestr);
        setAttr("wechatTimestamp", timestamp);
        setAttr("wechatSignature", signature);
    }

    public boolean isAllowVisit() {
        HttpServletRequest req = getRequest();
        if (RequestUtils.isWechatPcBrowser(req)) {
            return false;
        }

        return RequestUtils.isWechatBrowser(req);
    }

    public void doNotAlloVisitRedirect() {
        /**
         * 一般情况下，此方法是为了调整到其他页面，比如让用户扫描二维码之类的
         */
        renderText("不能访问");
    }


    public abstract Object doGetUserByOpenId(String openid);

    public abstract Object doSaveOrUpdateUserByApiResult(ApiResult apiResult);
}
