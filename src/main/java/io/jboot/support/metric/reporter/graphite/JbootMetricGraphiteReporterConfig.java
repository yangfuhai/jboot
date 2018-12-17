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
package io.jboot.support.metric.reporter.graphite;

import io.jboot.app.config.annotation.PropertyModel;

@PropertyModel(prefix = "jboot.metric.reporter.graphite")
public class JbootMetricGraphiteReporterConfig {


    private String host;
    private Integer port = 2003;

    private String prefixedWith;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPrefixedWith() {
        return prefixedWith;
    }

    public void setPrefixedWith(String prefixedWith) {
        this.prefixedWith = prefixedWith;
    }
}



