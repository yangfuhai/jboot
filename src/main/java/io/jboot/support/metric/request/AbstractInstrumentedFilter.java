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
package io.jboot.support.metric.request;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.jfinal.handler.Handler;
import io.jboot.Jboot;
import io.jboot.support.metric.JbootMetricConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * {@link Filter} implementation which captures request information and a breakdown of the response
 * codes being returned.
 */
public abstract class AbstractInstrumentedFilter extends Handler {

    private final Map<Integer, String> meterNamesByStatusCode;

    // initialized after call of init method
    private ConcurrentMap<Integer, Meter> metersByStatusCode;
    private Meter otherMeter;
    private Meter timeoutsMeter;
    private Meter errorsMeter;
    private Counter activeRequests;
    private Timer requestTimer;

    private JbootMetricConfig jbootMetricConfig = Jboot.config(JbootMetricConfig.class);


    /**
     * Creates a new instance of the filter.
     *
     * @param meterNamesByStatusCode A map, keyed by status code, of meter names that we are
     *                               interested in.
     * @param otherMetricName        The name used for the catch-all meter.
     */
    protected AbstractInstrumentedFilter(Map<Integer, String> meterNamesByStatusCode, String otherMetricName) {
        this.meterNamesByStatusCode = meterNamesByStatusCode;


        final MetricRegistry metricsRegistry = Jboot.getMetric();

        String metricName = jbootMetricConfig.getRequestMetricName();
        if (metricName == null || metricName.isEmpty()) {
            metricName = getClass().getName();
        }

        this.metersByStatusCode = new ConcurrentHashMap<>(meterNamesByStatusCode.size());
        for (Entry<Integer, String> entry : meterNamesByStatusCode.entrySet()) {
            metersByStatusCode.put(entry.getKey(),
                    metricsRegistry.meter(name(metricName, entry.getValue())));
        }
        this.otherMeter = metricsRegistry.meter(name(metricName, otherMetricName));
        this.timeoutsMeter = metricsRegistry.meter(name(metricName, "timeouts"));
        this.errorsMeter = metricsRegistry.meter(name(metricName, "errors"));
        this.activeRequests = metricsRegistry.counter(name(metricName, "activeRequests"));
        this.requestTimer = metricsRegistry.timer(name(metricName, "requests"));

    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        final StatusExposingServletResponse wrappedResponse = new StatusExposingServletResponse(response);
        activeRequests.inc();
        final Timer.Context context = requestTimer.time();
        boolean error = false;
        try {
            next.handle(target, request, wrappedResponse, isHandled);
        } catch (Exception e) {
            error = true;
            throw e;
        } finally {
            if (!error && request.isAsyncStarted()) {
                request.getAsyncContext().addListener(new AsyncResultListener(context));
            } else {
                context.stop();
                activeRequests.dec();
                if (error) {
                    errorsMeter.mark();
                } else {
                    markMeterForStatusCode(wrappedResponse.getStatus());
                }
            }
        }
    }

    private void markMeterForStatusCode(int status) {
        final Meter metric = metersByStatusCode.get(status);
        if (metric != null) {
            metric.mark();
        } else {
            otherMeter.mark();
        }
    }

    private static class StatusExposingServletResponse extends HttpServletResponseWrapper {
        // The Servlet spec says: calling setStatus is optional, if no status is set, the default is 200.
        private int httpStatus = 200;

        public StatusExposingServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void setStatus(int sc, String sm) {
            httpStatus = sc;
            super.setStatus(sc, sm);
        }

        @Override
        public int getStatus() {
            return httpStatus;
        }
    }

    private class AsyncResultListener implements AsyncListener {
        private Timer.Context context;
        private boolean done = false;

        public AsyncResultListener(Timer.Context context) {
            this.context = context;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            if (!done) {
                HttpServletResponse suppliedResponse = (HttpServletResponse) event.getSuppliedResponse();
                context.stop();
                activeRequests.dec();
                markMeterForStatusCode(suppliedResponse.getStatus());
            }
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            context.stop();
            activeRequests.dec();
            timeoutsMeter.mark();
            done = true;
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            context.stop();
            activeRequests.dec();
            errorsMeter.mark();
            done = true;
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {

        }
    }
}