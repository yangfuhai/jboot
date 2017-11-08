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
package io.jboot.component.swagger;

import io.jboot.config.annotation.PropertieConfig;
import io.jboot.utils.StringUtils;

@PropertieConfig(prefix = "jboot.swagger")
public class JbootSwaggerConfig {

    private String path;

    private String title;
    private String description;
    private String version;
    private String termsOfService;
    private String host;

    // 多个联系方式用分号；隔开  例如：jboot.swagger.contact=email:fuhai999@163.com;qq:1506615067
    private String contact;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isConfigOk() {
        return StringUtils.isNotBlank(path);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}



