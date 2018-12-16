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
package io.jboot.web.websocket;

import io.jboot.Jboot;
import io.jboot.kits.ClassScanner;
import io.jboot.kits.StringKits;
import io.jboot.web.JbootWebConfig;

import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class JbootWebsocketManager {
    private static JbootWebsocketManager manager = new JbootWebsocketManager();

    private Set<String> websocketEndPointValues = new HashSet<>();
    private Set<Class> websocketEndPoints = new HashSet<>();
    private JbootWebConfig config = Jboot.config(JbootWebConfig.class);

    private JbootWebsocketManager() {
        List<Class> endPointClasses = ClassScanner.scanClassByAnnotation(ServerEndpoint.class, false);
        if (endPointClasses != null && endPointClasses.size() != 0) {
            for (Class entry : endPointClasses) {
                ServerEndpoint serverEndpoint = (ServerEndpoint) entry.getAnnotation(ServerEndpoint.class);
                String value = serverEndpoint.value();
                if (!StringKits.isBlank(value)) {
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
        if (!config.isWebsocketEnable()) {
            return false;
        }

        if (config.getWebsocketBasePath() != null) {
            return endPointValue.startsWith(config.getWebsocketBasePath());
        }

        if (websocketEndPoints.isEmpty()) {
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
