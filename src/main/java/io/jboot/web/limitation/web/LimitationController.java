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
        int rate = getParaToInt("rate", 0);
        String type = getPara("type");

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

        renderJson(Ret.ok().set("message", "enable ok"));
    }


    private void setIpRates(String path, int rate) {
        LimitationInfo info = manager.getIpRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_IP);
        }
        info.setRate(rate);
        manager.getIpRates().put(path, info);
    }


    private void setUserRates(String path, int rate) {
        LimitationInfo info = manager.getUserRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_USER);
        }
        info.setRate(rate);
        manager.getUserRates().put(path, info);
    }


    private void setRequestRates(String path, int rate) {
        LimitationInfo info = manager.getRequestRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_REQUEST);
        }
        info.setRate(rate);
        manager.getRequestRates().put(path, info);
    }


    private void setConcurrencyRates(String path, int rate) {
        LimitationInfo info = manager.getConcurrencyRates().get(path);
        if (info == null) {
            info = new LimitationInfo();
            info.setType(LimitationInfo.TYPE_CONCURRENCY);
        }
        info.setRate(rate);
        manager.getConcurrencyRates().put(path, info);
    }
}
