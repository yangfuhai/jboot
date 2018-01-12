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
package io.jboot.component.metric;

import io.jboot.config.annotation.PropertyConfig;

@PropertyConfig(prefix = "jboot.metric")
public class JbootMetricConfig {

    public static final String REPORTER_JMX = "jmx";
    public static final String REPORTER_INFLUXDB = "influxdb";
    public static final String REPORTER_GRAPHITE = "graphite";
    public static final String REPORTER_ELASTICSEARCH = "elasticsearch";
    public static final String REPORTER_GANGLIA = "ganglia";
    public static final String REPORTER_CONSOLE = "console";
    public static final String REPORTER_CSV = "csv";
    public static final String REPORTER_SLF4J = "slf4j";

    private String url;
    private String reporter;

    public String getUrl() {
        //在metrics中，会访问到配置的二级目录，必须添加下 /* 才能正常访问
        if (url != null && !url.endsWith("/*")) {
            return url + "/*";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
}



