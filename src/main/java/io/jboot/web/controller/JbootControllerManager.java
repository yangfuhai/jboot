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
package io.jboot.web.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jfinal.core.Controller;
import com.jfinal.core.ControllerFactory;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootControllerManager extends ControllerFactory {

    private static final JbootControllerManager ME = new JbootControllerManager();

    public static JbootControllerManager me() {
        return ME;
    }

    private JbootControllerManager() {
    }


    private BiMap<String, Class<? extends Controller>> controllerMapping = HashBiMap.create();

    public Class<? extends Controller> getControllerByPath(String path) {
        return controllerMapping.get(path);
    }

    public String getPathByController(Class<? extends Controller> controllerClass) {
        return controllerMapping.inverse().get(controllerClass);
    }

    public void setMapping(String path, Class<? extends Controller> controllerClass) {
        controllerMapping.put(path, controllerClass);
    }


}