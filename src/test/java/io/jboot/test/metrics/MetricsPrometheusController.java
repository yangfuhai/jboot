package io.jboot.test.metrics;

import io.jboot.app.JbootApplication;
import io.jboot.support.metric.annotation.*;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/metrics/prometheus")
public class MetricsPrometheusController extends JbootController {

    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.metric.enable", "true");
        JbootApplication.setBootArg("jboot.metric.reporter", "prometheus");

//        JbootApplication.setBootArg("jboot.metric.reporter.prometheus.host", "127.0.0.1");
//        JbootApplication.setBootArg("jboot.metric.reporter.prometheus.port", "1234");

        JbootApplication.run(args);
    }

    @EnableMetricCounter
    @EnableMetricConcurrency
    @EnableMetricTimer
    @EnableMetricHistogram
    @EnableMetricMeter
    public void index() {
        renderText("metrics prometheus index. ");
    }
}