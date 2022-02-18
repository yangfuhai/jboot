/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.support.metric;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 参考 https://github.com/micrometer-metrics/micrometer/
 * blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/system/ProcessorMetrics.java
 */
public class JvmCpuGaugeSet implements MetricSet {


    /** List of public, exported interface class names from supported JVM implementations. */
    private static final List<String> OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList(
            "com.ibm.lang.management.OperatingSystemMXBean", // J9
            "com.sun.management.OperatingSystemMXBean" // HotSpot
    );


    private final OperatingSystemMXBean operatingSystemBean;

    private final Class<?> operatingSystemBeanClass;

    private final Method systemCpuUsage;

    private final Method processCpuUsage;

    /**
     * Creates a new set of gauges.
     */
    public JvmCpuGaugeSet() {
        this.operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
        this.operatingSystemBeanClass = getFirstClassFound(OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        Method getCpuLoad = detectMethod("getCpuLoad");
        this.systemCpuUsage = getCpuLoad != null ? getCpuLoad : detectMethod("getSystemCpuLoad");
        this.processCpuUsage = detectMethod("getProcessCpuLoad");
    }


    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();

        Runtime runtime = Runtime.getRuntime();
        gauges.put("system.cpu.count", (Gauge<Integer>) () -> runtime.availableProcessors());

        if (operatingSystemBean.getSystemLoadAverage() >= 0) {
            gauges.put("system.load.average.1m", (Gauge<Double>) () -> operatingSystemBean.getSystemLoadAverage());
        }

        if (systemCpuUsage != null) {
            gauges.put("system.cpu.usage", (Gauge<Double>) () -> invoke(systemCpuUsage));
        }

        if (processCpuUsage != null) {
            gauges.put("process.cpu.usage", (Gauge<Double>) () -> invoke(processCpuUsage));
        }

        return Collections.unmodifiableMap(gauges);
    }




    private double invoke(Method method) {
        try {
            return method != null ? (double) method.invoke(operatingSystemBean) : Double.NaN;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Double.NaN;
        }
    }


    private Method detectMethod(String name) {
        if (operatingSystemBeanClass == null) {
            return null;
        }
        try {
            // ensure the Bean we have is actually an instance of the interface
            operatingSystemBeanClass.cast(operatingSystemBean);
            return operatingSystemBeanClass.getDeclaredMethod(name);
        } catch (ClassCastException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private Class<?> getFirstClassFound(List<String> classNames) {
        for (String className : classNames) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }
}