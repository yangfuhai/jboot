/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.gateway;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import io.jboot.app.config.JbootConfigUtil;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.NamedThreadFactory;
import io.jboot.utils.StrUtil;
import io.jboot.web.render.JbootJsonRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/21
 */
public class JbootGatewayManager {

    private static JbootGatewayManager me = new JbootGatewayManager();

    public static JbootGatewayManager me() {
        return me;
    }

    private ConcurrentHashMap<String, JbootGatewayConfig> configMap;

    private ScheduledThreadPoolExecutor fixedScheduler;

    private NoneHealthUrlErrorRender noneHealthUrlErrorRender;

    private JbootGatewayManager() {
        Map<String, JbootGatewayConfig> configMap = JbootConfigUtil.getConfigModels(JbootGatewayConfig.class, "jboot.gateway");
        if (!configMap.isEmpty()) {
            for (Map.Entry<String, JbootGatewayConfig> e : configMap.entrySet()) {
                JbootGatewayConfig config = e.getValue();
                if (config.isConfigOk() && config.isEnable()) {
                    if (StrUtil.isBlank(config.getName())) {
                        config.setName(e.getKey());
                    }
                    registerConfig(config);
                }
            }
        }
    }


    public synchronized void registerConfig(JbootGatewayConfig config) {
        if (configMap == null) {
            configMap = new ConcurrentHashMap<>();
        }
        configMap.put(config.getName(), config);

        // 启动定时健康检查
        startHealthCheckIfNecessary();
    }


    public boolean isEnableAndConfigOk() {
        return configMap != null && !configMap.isEmpty();
    }


    /**
     * 开始健康检查
     * 多次执行，只会启动一次
     */
    private void startHealthCheckIfNecessary() {
        if (fixedScheduler == null) {
            synchronized (this) {
                if (fixedScheduler == null) {
                    fixedScheduler = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("jboot-gateway-health-check"));
                    //每 10s 进行一次健康检查
                    fixedScheduler.scheduleWithFixedDelay(() -> {
                        try {
                            doHealthCheck();
                        } catch (Exception ex) {
                            LogKit.error(ex.toString(), ex);
                        }
                    }, 10, 10, TimeUnit.SECONDS);
                }
            }
        }
    }


    /**
     * 健康检查
     */
    private void doHealthCheck() {
        for (JbootGatewayConfig config : configMap.values()) {
            if (config.isUriHealthCheckEnable() && StrUtil.isNotBlank(config.getUriHealthCheckPath())) {
                String[] uris = config.getUri();
                for (String uri : uris) {
                    String url = uri + config.getUriHealthCheckPath();
                    if (getHttpCode(url) == 200) {
                        config.removeUnHealthUri(uri);
                    } else {
                        config.addUnHealthUri(uri);
                    }
                }
            }
        }
    }

    private int getHttpCode(String url) {
        try {
            JbootHttpRequest req = JbootHttpRequest.create(url);
            req.setReadBody(false);
            return HttpUtil.handle(req).getResponseCode();
        } catch (Exception ex) {
            // do nothing
            return 0;
        }
    }


    public JbootGatewayConfig removeConfig(String name) {
        return configMap == null ? null : configMap.remove(name);
    }


    public JbootGatewayConfig getConfig(String name) {
        return configMap == null ? null : configMap.get(name);
    }


    public Map<String, JbootGatewayConfig> getConfigMap() {
        return configMap;
    }

    /**
     * 匹配可用的网关
     *
     * @param req 请求
     * @return 返回匹配到的网关配置
     */
    public JbootGatewayConfig matchingConfig(HttpServletRequest req) {
        if (configMap != null && !configMap.isEmpty()) {
            Iterator<JbootGatewayConfig> iterator = configMap.values().iterator();
            while (iterator.hasNext()) {
                JbootGatewayConfig config = iterator.next();
                if (config.matches(req)) {
                    return config;
                }
            }
        }
        return null;
    }

    public NoneHealthUrlErrorRender getNoneHealthUrlErrorRender() {
        return noneHealthUrlErrorRender;
    }

    public void setNoneHealthUrlErrorRender(NoneHealthUrlErrorRender noneHealthUrlErrorRender) {
        this.noneHealthUrlErrorRender = noneHealthUrlErrorRender;
    }

    public void renderNoneHealthUrl(JbootGatewayConfig config, HttpServletRequest request, HttpServletResponse response) {
        if (noneHealthUrlErrorRender != null) {
            noneHealthUrlErrorRender.render(config, request, response);
        } else {
            new JbootJsonRender(Ret.fail().set("message", "none health url in gateway.")).setContext(request, response).render();
        }
    }
}
