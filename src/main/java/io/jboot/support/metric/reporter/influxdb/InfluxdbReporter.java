/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.metric.reporter.influxdb;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import io.jboot.Jboot;
import io.jboot.support.metric.JbootMetricReporter;
import metrics_influxdb.HttpInfluxdbProtocol;

import java.util.concurrent.TimeUnit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * url : https://github.com/davidB/metrics-influxdb
 */
public class InfluxdbReporter implements JbootMetricReporter {

    @Override
    public void report(MetricRegistry metricRegistry) {

        JbootMetricInfluxdbReporterConfig config = Jboot.config(JbootMetricInfluxdbReporterConfig.class);


        final ScheduledReporter reporter = metrics_influxdb.InfluxdbReporter.forRegistry(metricRegistry)
                .protocol(new HttpInfluxdbProtocol("http"
                        , config.getHost()
                        , config.getPort()
                        , config.getUser()
                        , config.getPassword()
                        , config.getDbName()))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .skipIdleMetrics(false)
//                .tag("cluster", config.getTagCluster())
//                .tag("client", config.getTagClient())
//                .tag("server", serverIP)
//                .transformer(new CategoriesMetricMeasurementTransformer("module", "artifact"))
                .build();

        reporter.start(10, TimeUnit.SECONDS);
    }
}
