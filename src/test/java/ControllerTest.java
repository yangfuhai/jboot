import io.jboot.Jboot;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.db.dao.JbootDaoBase;
import io.jboot.db.model.JbootModel;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Named;
import javax.inject.Singleton;


@RequestMapping("/test")
public class ControllerTest extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.setBootArg("jboot.cache.type", "redis");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        Jboot.run(args);
    }


//    @Inject
    @EnableHystrixCommand
    ServiceTest serviceTest;

//    @JbootrpcService
//    ServiceInter serviceInter;


    public void index() {




        renderText("hello " + serviceTest.getName("aaa"));




    }


    @Singleton
    public static class ServiceTest extends JbootDaoBase {


        @Cacheable(name = "test")
        public String getName() {
            System.out.println("getName invoke!!!!!!");
            return "michael";
        }

        @Cacheable(name = "test",key = "#(id)")
        public String getName(@Named("id") String id) {
            System.out.println("getName invoke!!!!!!");
            return id;
        }


        @Override
        public JbootModel findById(Object id) {
            return null;
        }

        @Override
        public boolean deleteById(Object id) {
            return false;
        }
    }


    public static interface ServiceInter {
        public String hello();
    }


}
