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
 * blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/system/FileDescriptorMetrics.java
 */
public class ProcessFilesGaugeSet implements MetricSet {


    /**
     * List of public, exported interface class names from supported JVM implementations.
     */
    private static final List<String> UNIX_OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList(
            "com.sun.management.UnixOperatingSystemMXBean", // HotSpot
            "com.ibm.lang.management.UnixOperatingSystemMXBean" // J9
    );

    private final OperatingSystemMXBean osBean;

    private final Class<?> osBeanClass;

    private final Method openFilesMethod;

    private final Method maxFilesMethod;


    /**
     * Creates a new set of gauges.
     */
    public ProcessFilesGaugeSet() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.osBeanClass = getFirstClassFound(UNIX_OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        this.openFilesMethod = detectMethod("getOpenFileDescriptorCount");
        this.maxFilesMethod = detectMethod("getMaxFileDescriptorCount");
    }


    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();


        if (openFilesMethod != null) {
            gauges.put("process.files.open", (Gauge<Double>) () -> invoke(openFilesMethod));
        }

        if (maxFilesMethod != null) {
            gauges.put("process.files.max", (Gauge<Double>) () -> invoke(maxFilesMethod));
        }

        return Collections.unmodifiableMap(gauges);
    }


    private double invoke(Method method) {
        try {
            return method != null ? (double) (long) method.invoke(osBean) : Double.NaN;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Double.NaN;
        }
    }


    private Method detectMethod(String name) {
        if (osBeanClass == null) {
            return null;
        }
        try {
            // ensure the Bean we have is actually an instance of the interface
            osBeanClass.cast(osBean);
            return osBeanClass.getDeclaredMethod(name);
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