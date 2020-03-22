/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JbootGatewayConfig implements Serializable {

    private String name;
    private String uri;
    private boolean enable = false;
    private boolean sentinelEnable = false;
    private String sentinelBlockPage;

    private String[] pathEquals;
    private String[] pathContains;
    private String[] pathStartsWith;
    private String[] pathEndswith;


    private String[] hostEquals;
    private String[] hostContains;
    private String[] hostStartsWith;
    private String[] hostEndswith;


    private Map<String, String> queryEquals;
    private String[] queryContains;

//    暂时不支持 cookie
//    private Map<String, String> cookieEquals;
//    private String[] cookieContains;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isSentinelEnable() {
        return sentinelEnable;
    }

    public void setSentinelEnable(boolean sentinelEnable) {
        this.sentinelEnable = sentinelEnable;
    }

    public String getSentinelBlockPage() {
        return sentinelBlockPage;
    }

    public void setSentinelBlockPage(String sentinelBlockPage) {
        this.sentinelBlockPage = sentinelBlockPage;
    }

    public String[] getPathEquals() {
        return pathEquals;
    }

    public void setPathEquals(String[] pathEquals) {
        this.pathEquals = pathEquals;
    }

    public String[] getPathContains() {
        return pathContains;
    }

    public void setPathContains(String[] pathContains) {
        this.pathContains = pathContains;
    }

    public String[] getPathStartsWith() {
        return pathStartsWith;
    }

    public void setPathStartsWith(String[] pathStartsWith) {
        this.pathStartsWith = pathStartsWith;
    }

    public String[] getPathEndswith() {
        return pathEndswith;
    }

    public void setPathEndswith(String[] pathEndswith) {
        this.pathEndswith = pathEndswith;
    }

    public String[] getHostEquals() {
        return hostEquals;
    }

    public void setHostEquals(String[] hostEquals) {
        this.hostEquals = hostEquals;
    }

    public String[] getHostContains() {
        return hostContains;
    }

    public void setHostContains(String[] hostContains) {
        this.hostContains = hostContains;
    }

    public String[] getHostStartsWith() {
        return hostStartsWith;
    }

    public void setHostStartsWith(String[] hostStartsWith) {
        this.hostStartsWith = hostStartsWith;
    }

    public String[] getHostEndswith() {
        return hostEndswith;
    }

    public void setHostEndswith(String[] hostEndswith) {
        this.hostEndswith = hostEndswith;
    }

    public Map<String, String> getQueryEquals() {
        return queryEquals;
    }

    public void setQueryEquals(Map<String, String> queryEquals) {
        this.queryEquals = queryEquals;
    }

    public String[] getQueryContains() {
        return queryContains;
    }

    public void setQueryContains(String[] queryContains) {
        this.queryContains = queryContains;
    }

    public boolean isConfigOk(){
        return StrUtil.isNotBlank(uri);
    }


    public boolean matches(HttpServletRequest request){
        if (request == null){
            return false;
        }

        String path = request.getServletPath();
        if (pathEquals != null){
            for (String p : pathEquals){
                if (path.equals(p)){
                    return true;
                }
            }
        }

        if (pathContains != null){
            for (String p : pathContains){
                if (path.contains(p)){
                    return true;
                }
            }
        }

        if (pathStartsWith != null){
            for (String p : pathStartsWith){
                if (path.startsWith(p)){
                    return true;
                }
            }
        }

        if (pathEndswith != null){
            for (String p : pathEndswith){
                if (path.endsWith(p)){
                    return true;
                }
            }
        }

        String host = request.getServerName();
        if (hostEquals != null){
            for (String h : hostEquals){
                if (host.equals(h)){
                    return true;
                }
            }
        }

        if (hostContains != null){
            for (String h : hostContains){
                if (host.contains(h)){
                    return true;
                }
            }
        }

        if (hostStartsWith != null){
            for (String h : hostStartsWith){
                if (host.startsWith(h)){
                    return true;
                }
            }
        }

        if (hostEndswith != null){
            for (String h : hostStartsWith){
                if (host.endsWith(h)){
                    return true;
                }
            }
        }

        if (queryContains != null || queryEquals != null){
            Map<String,String> queryMap = queryStringToMap(request.getQueryString());
            if (queryMap != null && !queryMap.isEmpty()){

                if (queryContains != null) {
                    for (String q : queryContains) {
                        if (queryMap.containsKey(q)) {
                            return true;
                        }
                    }
                }

                if (queryEquals != null){
                    for (Map.Entry<String,String> e : queryEquals.entrySet()){
                        String queryValue = queryMap.get(e.getKey());
                        if (Objects.equals(queryValue,e.getValue())){
                            return true;
                        }
                    }
                }
            }
        }



        return false;
    }



    private static Map<String, String> queryStringToMap(String queryString) {
        if (StrUtil.isBlank(queryString)){
            return null;
        }
        String[] params = queryString.split("&");
        Map<String, String> resMap = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                resMap.put(key, value);
            }
        }
        return resMap;
    }
}
