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
package io.jboot.web.limitation.web;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.limitation.JbootLimitationManager;
import io.jboot.web.limitation.LimitationInfo;

import java.util.HashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 *
 * 使用步骤：
 * 1、通过 jboot.limitation.viewPaht 设置词controller 的访问路径，例如：设置为 /limitation
 * 2、浏览器访问 /limitation 查看所有限流情况
 * 3、浏览器访问 /limitation/set?path=/aaa/bbb/ccc&rate=111&type=ip 来这是单个ip限流情况
 * 4、浏览器访问 /limitation/close?path=/aaa/bbb/ccc&type=ip 来关闭/aaa/bbb/ccc对ip的限流情况
 * 5、浏览器访问 /limitation/enable?path=/aaa/bbb/ccc&type=ip 来开启/aaa/bbb/ccc对ip的限流情况
 *
 * 其他：
 * 由于设置限流非常重要，可以通过 jboot.limitation.webAuthorizer = com.xxx.MyAuthorizer 来设置访问的 /limitation 的授权控制
 * MyAuthorizer 要实现接口 io.jboot.web.limitation.web.Authorizer
 */
@Before(LimitationControllerInter.class)
public class LimitationController extends JbootController {

    JbootLimitationManager manager = JbootLimitationManager.me();

    public void index() {

        HashMap info = new HashMap();

        info.put("ipRates", manager.getIpRates());
        info.put("userRates", manager.getUserRates());
        info.put("requestRates", manager.getRequestRates());
        info.put("concurrencyRates", manager.getConcurrencyRates());

        renderJson(info);
    }

    public void set() {
        String path = getPara("path");
        String rateString = getPara("rate");
        String type = getPara("type");

        double rate = StringUtils.isBlank(rateString) ? 0 : Double.valueOf(rateString.trim());

        if (StringUtils.isBlank(type)) {
            renderJson(Ret.fail().set("message", "type is empty"));
            return;
        }

        if (StringUtils.isBlank(path)) {
            renderJson(Ret.fail().set("message", "path is empty"));
            return;
        }

        if (rate <= 0) {
            renderJson(Ret.fail().set("message", "rate is error"));
            return;
        }

        switch (type) {
            case "ip":
                setIpRates(path, rate);
                break;
            case "user":
                setUserRates(path, rate);
                break;
            case "request":
                setRequestRates(path, rate);
                break;
            case "concurrency":
                setConcurrencyRates(path, rate);
                break;
            default:
                renderJson(Ret.fail().set("message", "type is error"));
                return;
        }

        renderJson(Ret.ok().set("message", "set ok"));
    }


    public void enable() {
        Ret ret = doProcessEnable(true);
        if (ret.isOk()) {
            ret.set("message", "enable ok");
        }
        renderJson(ret);
    }

    public void close() {
        Ret ret = doProcessEnable(false);
        if (ret.isOk()) {
            ret.set("message", "close ok");
        }
        renderJson(ret);
    }

    private Ret doProcessEnable(boolean enable) {
        String path = getPara("path");
        String type = getPara("type");

        if (StringUtils.isBlank(type)) {
            return Ret.fail().set("message", "type is empty");
        }

        if (StringUtils.isBlank(path)) {
            return Ret.fail().set("message", "path is empty");
        }

        switch (type) {
            case "ip":
                LimitationInfo info = manager.getIpRates().get(path);
                if (info == null) {
                    return Ret.fail("message", "path not set");
                }
                info.setEnable(enable);
                manager.getIpRates().put(path, info);
                break;
            case "user":
                LimitationInfo userInfo = manager.getIpRates().get(path);
                if (userInfo == null) {
                    return Ret.fail("message", "path not set");
                }
                userInfo.setEnable(enable);
                manager.getIpRates().put(path, userInfo);
                break;
            case "request":
                LimitationInfo requestInfo = manager.getIpRates().get(path);
                if (requestInfo == null) {
                    return Ret.fail("message", "path not set");
                }
                requestInfo.setEnable(enable);
                manager.getIpRates().put(path, requestInfo);
                break;
            case "concurrency":
                LimitationInfo concurrencyInfo = manager.getIpRates().get(path);
                if (concurrencyInfo == null) {
                    return Ret.fail("message", "path not set");
                }
                concurrencyInfo.setEnable(enable);
                manager.getIpRates().put(path, concurrencyInfo);
                break;
            default:
                return Ret.fail().set("message", "type is error");
        }

        return Ret.ok();
    }


    private void setIpRates(String path, double rate) {
        LimitationInfo info = manager.getIpRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_IP);
        }
        info.setRate(rate);
        manager.getIpRates().put(path, info);
    }


    private void setUserRates(String path, double rate) {
        LimitationInfo info = manager.getUserRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_USER);
        }
        info.setRate(rate);
        manager.getUserRates().put(path, info);
    }


    private void setRequestRates(String path, double rate) {
        LimitationInfo info = manager.getRequestRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_REQUEST);
        }
        info.setRate(rate);
        manager.getRequestRates().put(path, info);
    }


    private void setConcurrencyRates(String path, double rate) {
        LimitationInfo info = manager.getConcurrencyRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_CONCURRENCY);
        }
        info.setRate(rate);
        manager.getConcurrencyRates().put(path, info);
    }
}
