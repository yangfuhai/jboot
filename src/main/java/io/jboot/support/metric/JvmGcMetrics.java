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

import com.codahale.metrics.*;
import com.jfinal.log.Log;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static io.jboot.support.metric.JvmGcUtil.*;

/**
 * Record metrics that report a number of statistics related to garbage
 * collection emanating from the MXBean and also adds information about GC causes.
 * <p>
 * This provides metrics for OpenJDK garbage collectors: serial, parallel, G1, Shenandoah, ZGC.
 *
 * @author Jon Schneider
 * @author Tommy Ludwig
 * @see GarbageCollectorMXBean
 */
public class JvmGcMetrics implements MetricSet, AutoCloseable {

    private final static Log log = Log.getLog(JvmGcMetrics.class);

    private final boolean managementExtensionsPresent = isManagementExtensionsPresent();

private MetricRegistry metricRegistry;

    private String youngGenPoolName;

    private String oldGenPoolName;

    private String nonGenerationalMemoryPool;

    private final List<Runnable> notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<>();


    public JvmGcMetrics(MetricRegistry metricRegistry) {
        this.metricRegistry  = metricRegistry;

        for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            String name = mbean.getName();
            if (isYoungGenPool(name)) {
                youngGenPoolName = name;
            } else if (isOldGenPool(name)) {
                oldGenPoolName = name;
            } else if (isNonGenerationalHeapPool(name)) {
                nonGenerationalMemoryPool = name;
            }
        }
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();
        if (!this.managementExtensionsPresent) {
            return gauges;
        }

        double maxLongLivedPoolBytes = getLongLivedHeapPool().map(mem -> getUsageValue(mem, MemoryUsage::getMax)).orElse(0.0);

        AtomicLong maxDataSize = new AtomicLong((long) maxLongLivedPoolBytes);
        gauges.put("jvm.gc.max.data.size", (Gauge<Long>) () -> maxDataSize.get());


        AtomicLong liveDataSize = new AtomicLong();
        gauges.put("jvm.gc.live.data.size",  (Gauge<Long>) () -> liveDataSize.get());

        Counter allocatedBytes =metricRegistry.counter("jvm.gc.memory.allocated");
        Counter promotedBytes =(oldGenPoolName == null) ? null : metricRegistry.counter("jvm.gc.memory.promoted");



        // start watching for GC notifications
        final AtomicLong heapPoolSizeAfterGc = new AtomicLong();

        for (GarbageCollectorMXBean mbean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!(mbean instanceof NotificationEmitter)) {
                continue;
            }
            NotificationListener notificationListener = (notification, ref) -> {
                CompositeData cd = (CompositeData) notification.getUserData();
                GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);

                String gcCause = notificationInfo.getGcCause();
                String gcAction = notificationInfo.getGcAction();
                GcInfo gcInfo = notificationInfo.getGcInfo();
                long duration = gcInfo.getDuration();
                if (isConcurrentPhase(gcCause, notificationInfo.getGcName())) {
//                    Timer.builder("jvm.gc.concurrent.phase.time")
//                            .tags(tags)
//                            .tags("action", gcAction, "cause", gcCause)
//                            .description("Time spent in concurrent phase")
//                            .register(registry)
//                            .record(duration, TimeUnit.MILLISECONDS);
                    metricRegistry.timer("jvm.gc.concurrent.phase.time")
                            .update(duration,TimeUnit.MICROSECONDS);
                } else {
//                    Timer.builder("jvm.gc.pause")
//                            .tags(tags)
//                            .tags("action", gcAction, "cause", gcCause)
//                            .description("Time spent in GC pause")
//                            .register(registry)
//                            .record(duration, TimeUnit.MILLISECONDS);
                    metricRegistry.timer("jvm.gc.pause")
                            .update(duration,TimeUnit.MICROSECONDS);
                }

                // Update promotion and allocation counters
                final Map<String, MemoryUsage> before = gcInfo.getMemoryUsageBeforeGc();
                final Map<String, MemoryUsage> after = gcInfo.getMemoryUsageAfterGc();

                if (nonGenerationalMemoryPool != null) {
                    countPoolSizeDelta(gcInfo.getMemoryUsageBeforeGc(), gcInfo.getMemoryUsageAfterGc(), allocatedBytes,
                            heapPoolSizeAfterGc, nonGenerationalMemoryPool);
                    if (after.get(nonGenerationalMemoryPool).getUsed() < before.get(nonGenerationalMemoryPool).getUsed()) {
                        liveDataSize.set(after.get(nonGenerationalMemoryPool).getUsed());
                        final long longLivedMaxAfter = after.get(nonGenerationalMemoryPool).getMax();
                        maxDataSize.set(longLivedMaxAfter);
                    }
                    return;
                }

                if (oldGenPoolName != null) {
                    final long oldBefore = before.get(oldGenPoolName).getUsed();
                    final long oldAfter = after.get(oldGenPoolName).getUsed();
                    final long delta = oldAfter - oldBefore;
                    if (delta > 0L) {
                        promotedBytes.inc(delta);
                    }

                    // Some GC implementations such as G1 can reduce the old gen size as part of a minor GC. To track the
                    // live data size we record the value if we see a reduction in the old gen heap size or
                    // after a major GC.
                    if (oldAfter < oldBefore || GcGenerationAge.fromName(notificationInfo.getGcName()) == GcGenerationAge.OLD) {
                        liveDataSize.set(oldAfter);
                        final long oldMaxAfter = after.get(oldGenPoolName).getMax();
                        maxDataSize.set(oldMaxAfter);
                    }
                }

                if (youngGenPoolName != null) {
                    countPoolSizeDelta(gcInfo.getMemoryUsageBeforeGc(), gcInfo.getMemoryUsageAfterGc(), allocatedBytes,
                            heapPoolSizeAfterGc, youngGenPoolName);
                }
            };
            NotificationEmitter notificationEmitter = (NotificationEmitter) mbean;
            notificationEmitter.addNotificationListener(notificationListener, notification -> notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION), null);
            notificationListenerCleanUpRunnables.add(() -> {
                try {
                    notificationEmitter.removeNotificationListener(notificationListener);
                } catch (ListenerNotFoundException ignore) {
                }
            });
        }

        return gauges;
    }

    private void countPoolSizeDelta(Map<String, MemoryUsage> before, Map<String, MemoryUsage> after, Counter counter,
                                    AtomicLong previousPoolSize, String poolName) {
        final long beforeBytes = before.get(poolName).getUsed();
        final long afterBytes = after.get(poolName).getUsed();
        final long delta = beforeBytes - previousPoolSize.get();
        previousPoolSize.set(afterBytes);
        if (delta > 0L) {
            counter.inc(delta);
        }
    }

    private static boolean isManagementExtensionsPresent() {
        if (ManagementFactory.getMemoryPoolMXBeans().isEmpty()) {
            // Substrate VM, for example, doesn't provide or support these beans (yet)
            log.warn("GC notifications will not be available because MemoryPoolMXBeans are not provided by the JVM");
            return false;
        }

        try {
            Class.forName("com.sun.management.GarbageCollectionNotificationInfo", false,
                    MemoryPoolMXBean.class.getClassLoader());
            return true;
        } catch (Throwable e) {
            // We are operating in a JVM without access to this level of detail
            log.warn("GC notifications will not be available because " +
                    "com.sun.management.GarbageCollectionNotificationInfo is not present");
            return false;
        }
    }

    @Override
    public void close() {
        notificationListenerCleanUpRunnables.forEach(Runnable::run);
    }

    /**
     * Generalization of which parts of the heap are considered "young" or "old" for multiple GC implementations
     */
    enum GcGenerationAge {
        OLD,
        YOUNG,
        UNKNOWN;

        private static Map<String, GcGenerationAge> knownCollectors = new HashMap<String, GcGenerationAge>() {{
            put("ConcurrentMarkSweep", OLD);
            put("Copy", YOUNG);
            put("G1 Old Generation", OLD);
            put("G1 Young Generation", YOUNG);
            put("MarkSweepCompact", OLD);
            put("PS MarkSweep", OLD);
            put("PS Scavenge", YOUNG);
            put("ParNew", YOUNG);
        }};

        static GcGenerationAge fromName(String name) {
            return knownCollectors.getOrDefault(name, UNKNOWN);
        }
    }

}
