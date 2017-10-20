/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.hystrix;

import io.jboot.config.annotation.PropertieConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PropertieConfig(prefix = "jboot.hystrix")
public class JbootHystrixConfig {

    private String url;
    private String propertie;

    // keys 的值为  key1:method1,method2;key2:method3,method4
    private String keys;
    private boolean closeAutoHystrix = false;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPropertie() {
        return propertie;
    }

    public void setPropertie(String propertie) {
        this.propertie = propertie;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public boolean isCloseAutoHystrix() {
        return closeAutoHystrix;
    }

    public void setCloseAutoHystrix(boolean closeAutoHystrix) {
        this.closeAutoHystrix = closeAutoHystrix;
    }

    private Map<String, String> methodKeyMapping = new ConcurrentHashMap<>();

    public String getKeyByMethod(String method) {
        if (keys != null && methodKeyMapping.isEmpty()) {
            initMapping();
        }

        return methodKeyMapping.get(method);
    }

    private void initMapping() {
        String keyMethodStrings[] = keys.split(";");
        for (String keyMethodString : keyMethodStrings) {
            String[] keyMethod = keyMethodString.split(":");
            if (keyMethod.length != 2) continue;

            String key = keyMethod[0];
            String[] methods = keyMethod[1].split(",");
            for (String method : methods) {
                methodKeyMapping.put(method, key);
            }
        }
    }
}



