import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;


@RequestMapping("/test")
public class ControllerTest extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.setBootArg("jboot.cache.type", "redis");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        Jboot.run(args);
    }


    @Inject
    ServiceInter serviceTest;

    public void index() {


        renderText("hello " + serviceTest.hello());


    }


    @Singleton
    @Bean
    public static class ServiceTest implements ServiceInter {


        @Override
        public String hello() {
            return "aaa";
        }
    }


    public static interface ServiceInter {
        public String hello();
    }


}
