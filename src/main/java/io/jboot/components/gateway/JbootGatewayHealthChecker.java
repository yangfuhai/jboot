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
package io.jboot.components.gateway;

import com.jfinal.kit.LogKit;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.NamedThreadFactory;
import io.jboot.utils.StrUtil;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/21
 */
public class JbootGatewayHealthChecker implements Runnable {

    private static JbootGatewayHealthChecker me = new JbootGatewayHealthChecker();

    public static JbootGatewayHealthChecker me() {
        return me;
    }


    private ScheduledThreadPoolExecutor fixedScheduler;
    private long fixedSchedulerInitialDelay = 10;
    private long fixedSchedulerDelay = 30;


    /**
     * 开始健康检查
     * 多次执行，只会启动一次
     */
    public synchronized void start() {
        if (fixedScheduler == null) {
            fixedScheduler = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("jboot-gateway-health-check"));
            fixedScheduler.scheduleWithFixedDelay(this, fixedSchedulerInitialDelay, fixedSchedulerDelay, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        fixedScheduler.shutdown();
        fixedScheduler = null;
    }


    @Override
    public void run() {
        try {
            doHealthCheck();
        } catch (Exception ex) {
            LogKit.error(ex.toString(), ex);
        }
    }

    /**
     * 健康检查
     */
    private void doHealthCheck() {
        for (JbootGatewayConfig config : JbootGatewayManager.me().getConfigMap().values()) {
            if (config.isEnable() && config.isUriHealthCheckEnable() && StrUtil.isNotBlank(config.getUriHealthCheckPath())) {
                Set<String> uris = config.getUri();
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

    public ScheduledThreadPoolExecutor getFixedScheduler() {
        return fixedScheduler;
    }

    public long getFixedSchedulerInitialDelay() {
        return fixedSchedulerInitialDelay;
    }

    public void setFixedSchedulerInitialDelay(long fixedSchedulerInitialDelay) {
        this.fixedSchedulerInitialDelay = fixedSchedulerInitialDelay;
    }

    public long getFixedSchedulerDelay() {
        return fixedSchedulerDelay;
    }

    public void setFixedSchedulerDelay(long fixedSchedulerDelay) {
        this.fixedSchedulerDelay = fixedSchedulerDelay;
    }
}
