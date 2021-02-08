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

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author michael yang (fuhai999@gmail.com)
 * 负载均衡策略
 */
public interface GatewayLoadBalanceStrategy {

    /**
     * 默认的负载均衡策略，随机返回一个 url
     */
    GatewayLoadBalanceStrategy DEFAULT_STRATEGY = (config, request) -> {
        String[] urls = config.getHealthUri();
        if (urls == null || urls.length == 0) {
            return null;
        } else if (urls.length == 1) {
            return urls[0];
        } else {
            return urls[ThreadLocalRandom.current().nextInt(urls.length)];
        }
    };

    String getUrl(JbootGatewayConfig config, HttpServletRequest request);
}
