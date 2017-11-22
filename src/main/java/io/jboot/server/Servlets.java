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
package io.jboot.server;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Servlets {
    Map<String, ServletInfo> servlets = new HashMap<>();


    public Map<String, ServletInfo> getServlets() {
        return servlets;
    }

    public void setServlets(Map<String, ServletInfo> servlets) {
        this.servlets = servlets;
    }

    public void addServlet(String name, ServletInfo info) {
        servlets.put(name, info);
    }

    public static class ServletInfo {
        private Class<? extends Servlet> servletClass;
        private List<String> urlMapping;

        public static ServletInfo create(Class<? extends Servlet> servletClass) {
            ServletInfo info = new ServletInfo();
            info.setServletClass(servletClass);
            return info;
        }

        public Class<? extends Servlet> getServletClass() {
            return servletClass;
        }

        public void setServletClass(Class<? extends Servlet> servletClass) {
            this.servletClass = servletClass;
        }

        public List<String> getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(List<String> urlMapping) {
            this.urlMapping = urlMapping;
        }

        public ServletInfo addUrlMapping(String url) {
            if (urlMapping == null) {
                urlMapping = new ArrayList<>();
            }
            urlMapping.add(url);
            return this;
        }
    }
}
