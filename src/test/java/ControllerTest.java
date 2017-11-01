import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;


@RequestMapping("/test")
public class ControllerTest extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.setBootArg("jboot.cache.type", "redis");
        Jboot.setBootArg("jboot.metrics.url", "/metrics.abc");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");

//        ClassScanner.scanClass();

        Jboot.run(args);


    }


    @Inject
    ServiceInter serviceTest;

    public void index() {

        System.out.println("aabbcc");

        Jboot.me().getMetrics().counter("myaction").inc();


//        serviceTest.test1();

        renderText("hello ddd : " + serviceTest.hello("michael"));


    }


    public void directive() {
        render("/test.html");
    }


    @Singleton
    @Bean
//    @Before(Tx.class)
    public static class ServiceTest implements ServiceInter {


        @Override
        @Cacheable(name = "aaa", key = "#(\"key:\" + aaa)")
        public String hello(String aaa) {
            System.out.println("hello invoked");
            return "aaa" + aaa;
        }

        @Override
        public String test1() {
            return null;
        }
    }


    public static interface ServiceInter {
        public String hello(String aaa);
        public String test1();
    }


}
