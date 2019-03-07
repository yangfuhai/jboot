package io.jboot.test.metrics;

import io.jboot.app.JbootApplication;
import io.jboot.support.metric.annotation.EnableMetricConcurrency;
import io.jboot.support.metric.annotation.EnableMetricCounter;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/metrics")
public class MetricsController extends JbootController {


    /**
     * 配置 reporter 为slf4j 输出
     *
     * 当用户访问的时候，log 定时会输出 index() 的访问次数和当前并发量，1分钟输出一次
     * 同时，配置  jboot.metric.url = /metrics_admin
     * 可以通过浏览器访问 /metrics_admin 查看当前的 index() 的并发量和访问次数
     *
     * PS：只有通过浏览器访问 http://127.0.0.1:8888/metrics 才会生成 metrics 记录
     *
     * @param args
     */
    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.metric.url", "/metrics_admin");
        JbootApplication.setBootArg("jboot.metric.reporter", "slf4j");

        JbootApplication.run(args);
    }

    @EnableMetricCounter
    @EnableMetricConcurrency
    public void index() {
        renderText("metrics index. ");
    }
}