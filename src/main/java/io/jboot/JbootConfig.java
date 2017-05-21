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
package io.jboot;

import io.jboot.config.annotation.PropertieConfig;
import io.jboot.web.JbootAppConfig;

@PropertieConfig(prefix = "jboot")
public class JbootConfig {

    private String mode ;
    private String bannerEnable = "true";
    private String bannerFile = "banner.txt";
    private String cron4jEnable = "true";
    private String cron4jFile = "cron4j.properties";
    private String jfinalConfig = JbootAppConfig.class.getName();


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBannerEnable() {
        return bannerEnable;
    }

    public void setBannerEnable(String bannerEnable) {
        this.bannerEnable = bannerEnable;
    }

    public String getBannerFile() {
        return bannerFile;
    }

    public void setBannerFile(String bannerFile) {
        this.bannerFile = bannerFile;
    }

    public boolean isBannerEnable() {
        return "true".equalsIgnoreCase(bannerEnable);
    }

    public String getCron4jEnable() {
        return cron4jEnable;
    }

    public void setCron4jEnable(String cron4jEnable) {
        this.cron4jEnable = cron4jEnable;
    }

    public String getCron4jFile() {
        return cron4jFile;
    }

    public void setCron4jFile(String cron4jFile) {
        this.cron4jFile = cron4jFile;
    }

    public boolean isCron4jEnable() {
        return "true".equalsIgnoreCase(cron4jEnable);
    }

    public String getJfinalConfig() {
        return jfinalConfig;
    }

    public void setJfinalConfig(String jfinalConfig) {
        this.jfinalConfig = jfinalConfig;
    }

    @Override
    public String toString() {
        return "JbootConfig {" +
                "mode='" + mode + '\'' +
                ", bannerEnable='" + bannerEnable + '\'' +
                ", bannerFile='" + bannerFile + '\'' +
                ", cron4jEnable='" + cron4jEnable + '\'' +
                ", cron4jFile='" + cron4jFile + '\'' +
                ", jfinalConfig='" + jfinalConfig + '\'' +
                '}';
    }
}
