package metrics;

import io.jboot.Jboot;
import io.jboot.component.metric.annotation.EnableMetricConcurrency;
import io.jboot.component.metric.annotation.EnableMetricCounter;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package metrics
 */
@RequestMapping("/metrics")
public class MetricsController extends JbootController {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.metric.url", "/metrics_admin");
        Jboot.setBootArg("jboot.metric.reporter", "slf4j");

        Jboot.run(args);
    }

    @EnableMetricCounter
    @EnableMetricConcurrency
    public void index() {
        renderText("metrics index. ");
    }
}
