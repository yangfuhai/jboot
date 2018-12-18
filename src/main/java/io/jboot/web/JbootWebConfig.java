/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web
 */
@ConfigModel(prefix = "jboot.web")
public class JbootWebConfig {

    public static final String ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT = "default";

    private boolean actionCacheEnable = true;
    private String actionCacheKeyGeneratorType = ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT;

    //websocket 的相关配置
    //具体使用请参考：https://github.com/undertow-io/undertow/tree/master/examples/src/main/java/io/undertow/examples/jsrwebsockets
    private boolean websocketEnable = false;
    private String websocketBasePath;
    private int websocketBufferPoolSize = 100;

    public static final String DEFAULT_COOKIE_ENCRYPT_KEY = "JBOOT_DEFAULT_ENCRYPT_KEY";
    private String cookieEncryptKey = DEFAULT_COOKIE_ENCRYPT_KEY;



    public boolean isActionCacheEnable() {
        return actionCacheEnable;
    }

    public void setActionCacheEnable(boolean actionCacheEnable) {
        this.actionCacheEnable = actionCacheEnable;
    }

    public String getActionCacheKeyGeneratorType() {
        return actionCacheKeyGeneratorType;
    }

    public void setActionCacheKeyGeneratorType(String actionCacheKeyGeneratorType) {
        this.actionCacheKeyGeneratorType = actionCacheKeyGeneratorType;
    }

    public boolean isWebsocketEnable() {
        return websocketEnable;
    }

    public void setWebsocketEnable(boolean websocketEnable) {
        this.websocketEnable = websocketEnable;
    }

    public int getWebsocketBufferPoolSize() {
        return websocketBufferPoolSize;
    }

    public void setWebsocketBufferPoolSize(int websocketBufferPoolSize) {
        this.websocketBufferPoolSize = websocketBufferPoolSize;
    }

    public String getCookieEncryptKey() {
        return cookieEncryptKey;
    }

    public void setCookieEncryptKey(String cookieEncryptKey) {
        this.cookieEncryptKey = cookieEncryptKey;
    }

    public String getWebsocketBasePath() {
        return websocketBasePath;
    }

    public void setWebsocketBasePath(String websocketBasePath) {
        this.websocketBasePath = websocketBasePath;
    }

    @Override
    public String toString() {
        return "JbootWebConfig {" +
                "actionCacheEnable=" + actionCacheEnable +
                ", actionCacheKeyGeneratorType='" + actionCacheKeyGeneratorType + '\'' +
                ", websocketEnable=" + websocketEnable +
                ", websocketBufferPoolSize=" + websocketBufferPoolSize +
                ", cookieEncryptKey='" + cookieEncryptKey + '\'' +
                '}';
    }
}
