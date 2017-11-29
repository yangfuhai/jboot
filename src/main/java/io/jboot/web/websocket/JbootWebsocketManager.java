/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.websocket;

import io.jboot.Jboot;
import io.jboot.server.JbootServerConfig;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class JbootWebsocketManager {
    private static JbootWebsocketManager manager = new JbootWebsocketManager();
    private static Set<String> websocketEndPointValues = new HashSet<>();
    private static Set<Class> websocketEndPoints = new HashSet<>();
    private JbootServerConfig serverConfig = Jboot.config(JbootServerConfig.class);

    private JbootWebsocketManager() {
        List<Class> endPointClasses = ClassScanner.scanClassByAnnotation(ServerEndpoint.class, false);
        if (endPointClasses != null && endPointClasses.size() != 0) {
            for (Class entry : endPointClasses) {
                ServerEndpoint serverEndpoint = (ServerEndpoint) entry.getAnnotation(ServerEndpoint.class);
                String value = serverEndpoint.value();
                if (!StringUtils.isBlank(value)) {
                    websocketEndPoints.add(entry);
                    websocketEndPointValues.add(value);
                }
            }
        }
    }

    public static JbootWebsocketManager me() {
        return manager;
    }

    public boolean isWebsokcetEndPoint(String endPointValue) {
        if (!serverConfig.isWebsocketEnable()) {
            return false;
        }
        return websocketEndPointValues.contains(endPointValue);
    }

    public Set<String> getWebsocketEndPointValues() {
        return websocketEndPointValues;
    }

    public Set<Class> getWebsocketEndPoints() {
        return websocketEndPoints;
    }
}
