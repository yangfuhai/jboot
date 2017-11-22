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
package io.jboot.component.swagger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 */
public class Swagger {

    private String swagger = "2.0";
    private SwaggerInfo info;
    private String host;
    private List<String> schemes = Lists.newArrayList("http", "https");
    private SwaggerTag[] tags;

    //key:path  Map:value
    private Map<String, Map> paths;
    private Map<String, Map> definitions;

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public SwaggerInfo getInfo() {
        return info;
    }

    public void setInfo(SwaggerInfo info) {
        this.info = info;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public SwaggerTag[] getTags() {
        return tags;
    }

    public void setTags(SwaggerTag[] tags) {
        this.tags = tags;
    }

    public Map<String, Map> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Map> paths) {
        this.paths = paths;
    }

    public void addPath(String pathString, Map pathInfo) {
        if (paths == null) {
            paths = Maps.newHashMap();
        }

        paths.put(pathString, pathInfo);
    }

    public Map<String, Map> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, Map> definitions) {
        this.definitions = definitions;
    }

    public void addDefinistion(String name, Map definition) {
        if (definitions == null) {
            definitions = Maps.newHashMap();
        }

        definitions.put(name, definition);
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }
}
