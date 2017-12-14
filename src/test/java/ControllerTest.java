import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;


@RequestMapping("/test")
public class ControllerTest extends Controller {


    public static void main(String[] args) {

//        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
//        Jboot.setBootArg("jboot.cache.type", "redis");
//        Jboot.setBootArg("jboot.metrics.url", "/metrics.abc");
//        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");


        Jboot.run(args);


    }


    public void json() {
        setAttr("aaa", "bbb");
        renderJson();
    }


    @Inject
    ServiceInter serviceTest;

    public void index() {

        renderText("hello" + serviceTest.hello(""));

    }


    public void directive() {
        render("/test.html");
    }

    public void directive1() {
        render("/test1.html");
    }


    @Singleton
    @Bean
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
