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

import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import io.jboot.Jboot;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.dropwizard.samplebuilder.DefaultSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrometheusExports extends io.prometheus.client.Collector implements io.prometheus.client.Collector.Describable {
    private static final Logger LOGGER = Logger.getLogger(DropwizardExports.class.getName());
    private MetricRegistry registry;
    private SampleBuilder sampleBuilder;
    private JbootApplicationConfig appConfig = Jboot.config(JbootApplicationConfig.class);

    /**
     * Creates a new DropwizardExports with a {@link DefaultSampleBuilder}.
     *
     * @param registry a metric registry to export in prometheus.
     */
    public PrometheusExports(MetricRegistry registry) {
        this.registry = registry;
        this.sampleBuilder = new DefaultSampleBuilder();
    }

    /**
     * @param registry      a metric registry to export in prometheus.
     * @param sampleBuilder sampleBuilder to use to create prometheus samples.
     */
    public PrometheusExports(MetricRegistry registry, SampleBuilder sampleBuilder) {
        this.registry = registry;
        this.sampleBuilder = sampleBuilder;
    }

    private static String getHelpMessage(String metricName, Metric metric) {
        return String.format("Generated from Jboot metric import (metric=%s, type=%s)",
                metricName, ClassUtil.getUsefulClass(metric.getClass()).getName());
    }

    // List<String> additionalLabelNames, List<String> additionalLabelValues,
    private List<String> getDefaultAdditionalLabelNames(String... names) {
        List<String> array = getDefaultAdditionalLabelNames();
        array.addAll(Arrays.asList(names));
        return array;
    }


    private List<String> getDefaultAdditionalLabelNames() {
        List<String> newArray = new ArrayList<>();
        newArray.add("application");
        newArray.add("instance");
        return newArray;
    }


    private List<String> getDefaultAdditionalLabelValues(String... values) {
        List<String> array = getDefaultAdditionalLabelValues();
        array.addAll(Arrays.asList(values));
        return array;
    }


    private List<String> getDefaultAdditionalLabelValues() {
        List<String> newArray = new ArrayList<>();
        newArray.add(appConfig.getName());
        newArray.add(getInstance());
        return newArray;
    }

    private static String instance = null;

    private static final String getInstance() {
        if (instance == null) {
            String ipAddress = getIpAddress();
            if (StrUtil.isNotBlank(ipAddress)) {
                instance = ipAddress + ":" + Jboot.configValue("undertow.port");
            } else {
                instance = "";
            }
        }
        return instance;
    }

    private static String getIpAddress() {
        String hostIpAddress = null;
        String siteLocalIpAddress = null;// 外网IP
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            boolean findSiteLocalIpAddress = false;// 是否找到外网IP
            while (networkInterfaces.hasMoreElements() && !findSiteLocalIpAddress) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();

                    if (!address.isSiteLocalAddress() && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 外网IP
                        siteLocalIpAddress = address.getHostAddress();
                        findSiteLocalIpAddress = true;
                        break;
                    } else if (address.isSiteLocalAddress()
                            && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 内网IP
                        hostIpAddress = address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 优先使用配置的外网IP地址
        return StrUtil.isNotBlank(siteLocalIpAddress) ? siteLocalIpAddress : hostIpAddress;
    }

    /**
     * Export counter as Prometheus <a href="https://prometheus.io/docs/concepts/metric_types/#gauge">Gauge</a>.
     */
    MetricFamilySamples fromCounter(String dropwizardName, Counter counter) {
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, ""
                , getDefaultAdditionalLabelNames()
                , getDefaultAdditionalLabelValues()
                , new Long(counter.getCount()).doubleValue());
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, counter), Arrays.asList(sample));
    }

    /**
     * Export gauge as a prometheus gauge.
     */
    MetricFamilySamples fromGauge(String dropwizardName, Gauge gauge) {
        Object obj = gauge.getValue();
        double value;
        if (obj instanceof Number) {
            value = ((Number) obj).doubleValue();
        } else if (obj instanceof Boolean) {
            value = ((Boolean) obj) ? 1 : 0;
        } else {
            LOGGER.log(Level.FINE, String.format("Invalid type for Gauge %s: %s", sanitizeMetricName(dropwizardName),
                    obj == null ? "null" : obj.getClass().getName()));
            return null;
        }
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, ""
                , getDefaultAdditionalLabelNames()
                , getDefaultAdditionalLabelValues()
                , value);
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, gauge), Arrays.asList(sample));
    }

    /**
     * Export a histogram snapshot as a prometheus SUMMARY.
     *
     * @param dropwizardName metric name.
     * @param snapshot       the histogram snapshot.
     * @param count          the total sample count for this snapshot.
     * @param factor         a factor to apply to histogram values.
     */
    MetricFamilySamples fromSnapshotAndCount(String dropwizardName, Snapshot snapshot, long count, double factor, String helpMessage) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.5"), snapshot.getMedian() * factor),
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.75"), snapshot.get75thPercentile() * factor),
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.95"), snapshot.get95thPercentile() * factor),
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.98"), snapshot.get98thPercentile() * factor),
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.99"), snapshot.get99thPercentile() * factor),
                sampleBuilder.createSample(dropwizardName, "", getDefaultAdditionalLabelNames("quantile"), getDefaultAdditionalLabelValues("0.999"), snapshot.get999thPercentile() * factor),
                sampleBuilder.createSample(dropwizardName, "_count"
                        , getDefaultAdditionalLabelNames()
                        , getDefaultAdditionalLabelValues()
                        , count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }

    /**
     * Convert histogram snapshot.
     */
    MetricFamilySamples fromHistogram(String dropwizardName, Histogram histogram) {
        return fromSnapshotAndCount(dropwizardName, histogram.getSnapshot(), histogram.getCount(), 1.0,
                getHelpMessage(dropwizardName, histogram));
    }

    /**
     * Export Dropwizard Timer as a histogram. Use TIME_UNIT as time unit.
     */
    MetricFamilySamples fromTimer(String dropwizardName, Timer timer) {
        return fromSnapshotAndCount(dropwizardName, timer.getSnapshot(), timer.getCount(),
                1.0D / TimeUnit.SECONDS.toNanos(1L), getHelpMessage(dropwizardName, timer));
    }

    /**
     * Export a Meter as as prometheus COUNTER.
     */
    MetricFamilySamples fromMeter(String dropwizardName, Meter meter) {
        final MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, "_total"
                , getDefaultAdditionalLabelNames()
                , getDefaultAdditionalLabelValues()
                , meter.getCount());
        return new MetricFamilySamples(sample.name, Type.COUNTER, getHelpMessage(dropwizardName, meter),
                Arrays.asList(sample));
    }

    @Override
    public List<MetricFamilySamples> collect() {
        Map<String, MetricFamilySamples> mfSamplesMap = new HashMap<String, MetricFamilySamples>();

        for (SortedMap.Entry<String, Gauge> entry : registry.getGauges().entrySet()) {
            addToMap(mfSamplesMap, fromGauge(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Counter> entry : registry.getCounters().entrySet()) {
            addToMap(mfSamplesMap, fromCounter(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Histogram> entry : registry.getHistograms().entrySet()) {
            addToMap(mfSamplesMap, fromHistogram(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Timer> entry : registry.getTimers().entrySet()) {
            addToMap(mfSamplesMap, fromTimer(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Meter> entry : registry.getMeters().entrySet()) {
            addToMap(mfSamplesMap, fromMeter(entry.getKey(), entry.getValue()));
        }
        return new ArrayList<MetricFamilySamples>(mfSamplesMap.values());
    }

    private void addToMap(Map<String, MetricFamilySamples> mfSamplesMap, MetricFamilySamples newMfSamples) {
        if (newMfSamples != null) {
            MetricFamilySamples currentMfSamples = mfSamplesMap.get(newMfSamples.name);
            if (currentMfSamples == null) {
                mfSamplesMap.put(newMfSamples.name, newMfSamples);
            } else {
                List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>(currentMfSamples.samples);
                samples.addAll(newMfSamples.samples);
                mfSamplesMap.put(newMfSamples.name, new MetricFamilySamples(newMfSamples.name, currentMfSamples.type, currentMfSamples.help, samples));
            }
        }
    }

    @Override
    public List<MetricFamilySamples> describe() {
        return new ArrayList<MetricFamilySamples>();
    }
}