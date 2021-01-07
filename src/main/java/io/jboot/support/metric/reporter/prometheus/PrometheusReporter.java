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
package io.jboot.support.metric.reporter.prometheus;

import com.codahale.metrics.MetricRegistry;
import io.jboot.Jboot;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.metric.JbootMetricReporter;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @url https://github.com/prometheus/client_java
 */
public class PrometheusReporter implements JbootMetricReporter {

    private HTTPServer httpServer;

    public PrometheusReporter() {
        PrometheusReporterConfig config = Jboot.config(PrometheusReporterConfig.class);
        try {
            httpServer = new HTTPServer(config.getHost(), config.getPort());
            String printMsg = "Prometheus Reporter Server started -> http://" + config.getHost() + ":" + config.getPort();
            System.out.println(printMsg);
        } catch (IOException e) {
            throw new JbootIllegalConfigException("Prometheus config is error, please check your jboot.properties. ", e);
        }

//        if (httpServer != null) {
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                if (httpServer != null) {
//                    try {
//                        httpServer.stop();
//                    } catch (Exception ex) {
//                    }
//                }
//            }, "prometheus-httpserver-hook"));
//        }
    }

    @Override
    public void report(MetricRegistry metricRegistry) {
//        new DropwizardExports(metricRegistry).register();

        // 使用 PrometheusExports 主要是可以添加 application 和 instance 的参数
        // 例如 jvm_memory_total_used{application="jboot",instance="192.168.3.24:8818",} 1.521354E8
        new PrometheusExports(metricRegistry).register();
    }
}
