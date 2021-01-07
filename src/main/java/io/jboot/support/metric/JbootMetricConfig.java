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
package io.jboot.support.metric;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

@ConfigModel(prefix = "jboot.metric")
public class JbootMetricConfig {

    public static final String REPORTER_JMX = "jmx";
    public static final String REPORTER_INFLUXDB = "influxdb";
    public static final String REPORTER_GRAPHITE = "graphite";
    public static final String REPORTER_ELASTICSEARCH = "elasticsearch";
    public static final String REPORTER_GANGLIA = "ganglia";
    public static final String REPORTER_CONSOLE = "console";
    public static final String REPORTER_CSV = "csv";
    public static final String REPORTER_SLF4J = "slf4j";
    public static final String REPORTER_PROMETHEUS= "prometheus";

    private boolean enable = false;

    private String adminServletMapping;
    private String reporter;

    //是否启用 jvm 监控
    private boolean jvmMetricEnable = true;

    //是否启用请求监控
    private boolean requestMetricEnable = true;
    private String requestMetricName = "jboot-request";


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAdminServletMapping() {
        return adminServletMapping;
    }

    public void setAdminServletMapping(String adminServletMapping) {
        this.adminServletMapping = adminServletMapping;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public boolean isJvmMetricEnable() {
        return jvmMetricEnable;
    }

    public void setJvmMetricEnable(boolean jvmMetricEnable) {
        this.jvmMetricEnable = jvmMetricEnable;
    }

    public boolean isRequestMetricEnable() {
        return requestMetricEnable;
    }

    public void setRequestMetricEnable(boolean requestMetricEnable) {
        this.requestMetricEnable = requestMetricEnable;
    }

    public String getRequestMetricName() {
        return requestMetricName;
    }

    public void setRequestMetricName(String requestMetricName) {
        this.requestMetricName = requestMetricName;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotBlank(reporter);
    }
}



