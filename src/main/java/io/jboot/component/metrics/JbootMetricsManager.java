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
package io.jboot.component.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.component.metrics.reporter.console.JbootConsoleReporter;
import io.jboot.component.metrics.reporter.csv.CSVReporter;
import io.jboot.component.metrics.reporter.elasticsearch.ElasticsearchReporter;
import io.jboot.component.metrics.reporter.ganglia.GangliaReporter;
import io.jboot.component.metrics.reporter.graphite.JbootGraphiteReporter;
import io.jboot.component.metrics.reporter.influxdb.InfluxdbReporter;
import io.jboot.component.metrics.reporter.jmx.JMXReporter;
import io.jboot.component.metrics.reporter.slf4j.JbootSlf4jReporter;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class JbootMetricsManager {

    private static JbootMetricsManager me = new JbootMetricsManager();
    private static final Log LOG = Log.getLog(JbootMetricsManager.class);


    public static JbootMetricsManager me() {
        return me;
    }

    private MetricRegistry metricRegistry;
    private HealthCheckRegistry healthCheckRegistry;
    private JbootMetricsConfig metricsConfig = Jboot.config(JbootMetricsConfig.class);


    private JbootMetricsManager() {
        metricRegistry = new MetricRegistry();
        healthCheckRegistry = new HealthCheckRegistry();
    }

    
    public void init() {
        List<JbootMetricsReporter> reporters = getReporters();
        if (ArrayUtils.isNullOrEmpty(reporters)) {
            LOG.warn("metrics reporter is empty in application.");
            return;
        }

        for (JbootMetricsReporter reporter : reporters) {
            try {
                reporter.report(metricRegistry);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }
    }


    private List<JbootMetricsReporter> getReporters() {
        String repoterString = metricsConfig.getReporter();
        if (StringUtils.isBlank(repoterString)) {
            return null;
        }
        List<JbootMetricsReporter> reporters = new ArrayList<>();

        String[] repoterStrings = repoterString.split(";");
        for (String repoterName : repoterStrings) {
            JbootMetricsReporter reporter = getReporterByName(repoterName);
            if (reporter != null) {
                reporters.add(reporter);
            }
        }

        return reporters;
    }


    private JbootMetricsReporter getReporterByName(String repoterName) {

        JbootMetricsReporter reporter = null;

        switch (repoterName) {
            case JbootMetricsConfig.REPORTER_JMX:
                reporter = new JMXReporter();
                break;
            case JbootMetricsConfig.REPORTER_INFLUXDB:
                reporter = new InfluxdbReporter();
                break;
            case JbootMetricsConfig.REPORTER_GRAPHITE:
                reporter = new JbootGraphiteReporter();
                break;
            case JbootMetricsConfig.REPORTER_ELASTICSEARCH:
                reporter = new ElasticsearchReporter();
                break;
            case JbootMetricsConfig.REPORTER_GANGLIA:
                reporter = new GangliaReporter();
                break;
            case JbootMetricsConfig.REPORTER_CONSOLE:
                reporter = new JbootConsoleReporter();
                break;
            case JbootMetricsConfig.REPORTER_CSV:
                reporter = new CSVReporter();
                break;
            case JbootMetricsConfig.REPORTER_SLF4J:
                reporter = new JbootSlf4jReporter();
                break;
            default:
                reporter = JbootSpiLoader.load(JbootMetricsReporter.class, repoterName);
        }

        return reporter;
    }


    public MetricRegistry metric() {
        return metricRegistry;
    }

    public HealthCheckRegistry healthCheck() {
        return healthCheckRegistry;
    }


}
