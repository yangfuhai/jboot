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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.jvm.*;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.support.metric.reporter.console.JbootConsoleReporter;
import io.jboot.support.metric.reporter.csv.CSVReporter;
import io.jboot.support.metric.reporter.elasticsearch.ElasticsearchReporter;
import io.jboot.support.metric.reporter.ganglia.GangliaReporter;
import io.jboot.support.metric.reporter.graphite.JbootGraphiteReporter;
import io.jboot.support.metric.reporter.influxdb.InfluxdbReporter;
import io.jboot.support.metric.reporter.jmx.JMXReporter;
import io.jboot.support.metric.reporter.prometheus.PrometheusReporter;
import io.jboot.support.metric.reporter.slf4j.JbootSlf4jReporter;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


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

    private boolean enable = false;

    private JbootMetricManager() {

        if (!metricsConfig.isConfigOk() || !metricsConfig.isEnable()) {
            return;
        }

        metricRegistry = new MetricRegistry();
        healthCheckRegistry = new HealthCheckRegistry();

        if (metricsConfig.isJvmMetricEnable()) {
            metricRegistry.register("jvm.uptime", (Gauge<Long>) () -> ManagementFactory.getRuntimeMXBean().getUptime());
            metricRegistry.register("jvm.current_time", (Gauge<Long>) () -> System.nanoTime());
            metricRegistry.register("jvm.classes", new ClassLoadingGaugeSet());
            metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
            metricRegistry.register("jvm.fd", new FileDescriptorRatioGauge());
            metricRegistry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
            metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
            metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
            metricRegistry.register("jvm.threads", new CachedThreadStatesGaugeSet(10, TimeUnit.SECONDS));

            healthCheckRegistry.register("jvm.thread_deadlocks", new ThreadDeadlockHealthCheck());
        }


        List<JbootMetricReporter> reporters = getReporters();
        if (ArrayUtil.isNullOrEmpty(reporters)) {
            return;
        }

        for (JbootMetricReporter reporter : reporters) {
            try {
                reporter.report(metricRegistry);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }

        this.enable = true;
    }


    public boolean isEnable() {
        return enable;
    }

    private List<JbootMetricReporter> getReporters() {
        String repoterString = metricsConfig.getReporter();
        if (StrUtil.isBlank(repoterString)) {
            return null;
        }
        List<JbootMetricReporter> reporters = new ArrayList<>();

        String[] repoterStrings = repoterString.split(",");
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
            case JbootMetricConfig.REPORTER_PROMETHEUS:
                reporter = new PrometheusReporter();
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
