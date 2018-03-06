import io.jboot.Jboot;

public class MetricTest {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.metric.url", "/metrics.html");
        Jboot.setBootArg("jboot.metric.jmxReporter", true);
        Jboot.run(args);

    }


}


