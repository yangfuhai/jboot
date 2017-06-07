import io.jboot.Jboot;

public class MetricsTest {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.metrics.url", "/metrics.html/*");
        Jboot.setBootArg("jboot.metrics.jmxReporter", true);
        Jboot.run(args);


    }


}


