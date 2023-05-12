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
package io.jboot.app.undertow;

import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import io.jboot.app.config.JbootConfigManager;
import io.undertow.servlet.Servlets;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;


public class JbootUndertowServer extends UndertowServer {

    public JbootUndertowServer(UndertowConfig undertowConfig) {
        super(undertowConfig);
    }

    /**
     * 添加 自定义 filter 的支持
     */
    @Override
    protected void configJFinalFilter() {
        deploymentInfo.addFilter(
                Servlets.filter("jfinal", getJFinalFilter()).addInitParam("configClass", config.getJFinalConfig())
        ).addFilterUrlMapping("jfinal", "/*", DispatcherType.REQUEST);
    }


    private Class<? extends Filter> getJFinalFilter() {
        try {
            String jfinalFilter = JbootConfigManager.me().getConfigValue("undertow.jfinalFilter");
            if (jfinalFilter == null || jfinalFilter.trim().length() == 0){
                jfinalFilter = "com.jfinal.core.JFinalFilter";
            }
            return (Class<? extends Filter>)config.getClassLoader().loadClass(jfinalFilter);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
