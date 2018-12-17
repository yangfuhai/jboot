/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app;

import io.jboot.app.config.annotation.PropertyModel;

@PropertyModel(prefix = "jboot.app")
public class JbootApplicationConfig {

    private String mode = "dev";
    private boolean bannerEnable = true;
    private String bannerFile = "banner.txt";
    private String jfinalConfig = "io.jboot.web.JbootAppConfig";


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isBannerEnable() {
        return bannerEnable;
    }

    public void setBannerEnable(boolean bannerEnable) {
        this.bannerEnable = bannerEnable;
    }

    public String getBannerFile() {
        return bannerFile;
    }

    public void setBannerFile(String bannerFile) {
        this.bannerFile = bannerFile;
    }


    public String getJfinalConfig() {
        return jfinalConfig;
    }

    public void setJfinalConfig(String jfinalConfig) {
        this.jfinalConfig = jfinalConfig;
    }


    @Override
    public String toString() {
        return "JbootApplication {" +
                ", mode='" + mode + '\'' +
                ", bannerEnable=" + bannerEnable +
                ", bannerFile='" + bannerFile + '\'' +
                ", jfinalConfig='" + jfinalConfig + '\'' +
                '}';
    }
}
