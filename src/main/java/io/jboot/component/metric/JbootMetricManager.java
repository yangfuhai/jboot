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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.component.metric.reporter.console.JbootConsoleReporter;
import io.jboot.component.metric.reporter.csv.CSVReporter;
import io.jboot.component.metric.reporter.elasticsearch.ElasticsearchReporter;
import io.jboot.component.metric.reporter.ganglia.GangliaReporter;
import io.jboot.component.metric.reporter.graphite.JbootGraphiteReporter;
import io.jboot.component.metric.reporter.influxdb.InfluxdbReporter;
import io.jboot.component.metric.reporter.jmx.JMXReporter;
import io.jboot.component.metric.reporter.slf4j.JbootSlf4jReporter;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class JbootMetricManager {

    private static final Log LOG = Log.getLog(JbootMetricManager.class);

    private static JbootMetricManager me;

    public static JbootMetricManager me() {
        if (me == null) {
            me = new JbootMetricManager();
        }
        return me;
    }

    private MetricRegistry metricRegistry;
    private HealthCheckRegistry healthCheckRegistry;
    private JbootMetricConfig metricsConfig = Jboot.config(JbootMetricConfig.class);


    private JbootMetricManager() {
        metricRegistry = new MetricRegistry();
        healthCheckRegistry = new HealthCheckRegistry();

        List<JbootMetricReporter> reporters = getReporters();
        if (ArrayUtils.isNullOrEmpty(reporters)) {
            LOG.warn("metrics reporter is empty . please config \"jboot.metric.reporter = xxx\" in jboot.properties ");
            return;
        }

        for (JbootMetricReporter reporter : reporters) {
            try {
                reporter.report(metricRegistry);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }
    }


    private List<JbootMetricReporter> getReporters() {
        String repoterString = metricsConfig.getReporter();
        if (StringUtils.isBlank(repoterString)) {
            return null;
        }
        List<JbootMetricReporter> reporters = new ArrayList<>();

        String[] repoterStrings = repoterString.split(";");
        for (String repoterName : repoterStrings) {
            JbootMetricReporter reporter = getReporterByName(repoterName);
            if (reporter != null) {
                reporters.add(reporter);
            }
        }

        return reporters;
    }


    private JbootMetricReporter getReporterByName(String repoterName) {

        JbootMetricReporter reporter = null;

        switch (repoterName) {
            case JbootMetricConfig.REPORTER_JMX:
                reporter = new JMXReporter();
                break;
            case JbootMetricConfig.REPORTER_INFLUXDB:
                reporter = new InfluxdbReporter();
                break;
            case JbootMetricConfig.REPORTER_GRAPHITE:
                reporter = new JbootGraphiteReporter();
                break;
            case JbootMetricConfig.REPORTER_ELASTICSEARCH:
                reporter = new ElasticsearchReporter();
                break;
            case JbootMetricConfig.REPORTER_GANGLIA:
                reporter = new GangliaReporter();
                break;
            case JbootMetricConfig.REPORTER_CONSOLE:
                reporter = new JbootConsoleReporter();
                break;
            case JbootMetricConfig.REPORTER_CSV:
                reporter = new CSVReporter();
                break;
            case JbootMetricConfig.REPORTER_SLF4J:
                reporter = new JbootSlf4jReporter();
                break;
            default:
                reporter = JbootSpiLoader.load(JbootMetricReporter.class, repoterName);
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
