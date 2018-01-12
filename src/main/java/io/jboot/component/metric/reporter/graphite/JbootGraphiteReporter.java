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
package io.jboot.component.metric.reporter.graphite;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import io.jboot.Jboot;
import io.jboot.component.metric.JbootMetricReporter;
import io.jboot.utils.StringUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.metrics.reporter.graphite
 */
public class JbootGraphiteReporter implements JbootMetricReporter {
    @Override
    public void report(MetricRegistry metricRegistry) {

        JbootMetricsGraphiteReporterConfig config = Jboot.config(JbootMetricsGraphiteReporterConfig.class);

        if (StringUtils.isBlank(config.getHost())) {
            throw new NullPointerException("graphite reporter host must not be null, please config jboot.metrics.reporter.graphite.host in you properties.");
        }
        if (config.getPort() == null) {
            throw new NullPointerException("graphite reporter port must not be null, please config jboot.metrics.reporter.graphite.port in you properties.");
        }
        if (config.getPrefixedWith() == null) {
            throw new NullPointerException("graphite reporter prefixedWith must not be null, please config jboot.metrics.reporter.graphite.prefixedWith in you properties.");
        }

        Graphite graphite = new Graphite(new InetSocketAddress(config.getHost(), config.getPort()));

        GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(config.getPrefixedWith())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(1, TimeUnit.MINUTES);
    }
}
