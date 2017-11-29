/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.websocket;

import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import javax.websocket.server.ServerEndpoint;
import java.util.*;


public class JbootWebsocketManager {
    private static JbootWebsocketManager manager;
    private static Vector<String> serverEndPointValues = new Vector<String>();;

    public static JbootWebsocketManager me() {
        if (manager == null) {
            List<Class> endPointClasses = ClassScanner.scanClassByAnnotation(ServerEndpoint.class, false);
            if (endPointClasses != null && endPointClasses.size() != 0) {
                for (Class entry : endPointClasses) {
                    ServerEndpoint serverEndpoint = (ServerEndpoint) entry.getAnnotation(ServerEndpoint.class);
                    String value = serverEndpoint.value();
                    if (!StringUtils.isBlank(value)) {
                        serverEndPointValues.add(value);
                    }
                }
            }

            manager = ClassNewer.singleton(JbootWebsocketManager.class);
        }
        return manager;
    }

    public boolean containsEndPoint(String endPointValue) {
        return serverEndPointValues.contains(endPointValue);
    }

}
