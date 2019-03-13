package io.jboot.test.metrics;

import io.jboot.app.JbootApplication;
import io.jboot.support.metric.annotation.*;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/metrics/influxdb")
public class MetricsInfluxdbController extends JbootController {

    /**
     * influxdb 启动方式：
     * docker run -d -p 8086:8086 -p 8083:8083 \
     *     -e INFLUXDB_ADMIN_ENABLED=true \
     *     -e INFLUXDB_DB=metricsDb \
     *     -e INFLUXDB_ADMIN_USER=admin \
     *     -e INFLUXDB_ADMIN_PASSWORD=123456 \
     *     -e INFLUXDB_USER=fuhai \
     *     -e INFLUXDB_USER_PASSWORD=123456 \
     *     influxdb
     *
     *
     * grafana 启动方式：
     *     docker run -d -p 3000:3000 grafana/grafana
     */

    /**
     * 配置 reporter 为slf4j 输出
     *
     * 当用户访问的时候，log 定时会输出 index() 的访问次数和当前并发量，1分钟输出一次
     * 同时，配置  jboot.metric.url = /metrics_admin
     * 可以通过浏览器访问 /metrics_admin 查看当前的 index() 的并发量和访问次数
     *
     * PS：只有通过浏览器访问 http://127.0.0.1:8888/metrics/influxdb 才会生成 metrics 记录
     *
     * @param args
     */
    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.metric.url", "/metrics_admin");
        JbootApplication.setBootArg("jboot.metric.reporter", "influxdb");

        JbootApplication.setBootArg("jboot.metric.reporter.influxdb.host", "127.0.0.1");
        JbootApplication.setBootArg("jboot.metric.reporter.influxdb.port", 8086);
        JbootApplication.setBootArg("jboot.metric.reporter.influxdb.user", "admin");
        JbootApplication.setBootArg("jboot.metric.reporter.influxdb.password", "123456");
        JbootApplication.setBootArg("jboot.metric.reporter.influxdb.dbName", "metricsDb");

        JbootApplication.run(args);
    }

    @EnableMetricCounter
    @EnableMetricConcurrency
    @EnableMetricTimer
    @EnableMetricHistogram
    @EnableMetricMeter
    public void index() {
        renderText("metrics influxdb index. ");
    }
}