import io.jboot.Jboot;
import io.jboot.core.hystrix.annotation.EnableHystrixCommand;
import io.jboot.db.model.JbootModel;
import io.jboot.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;


@RequestMapping("/test")
public class ControllerTest extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.run(args);
    }


    @Inject
    @EnableHystrixCommand
    ServiceTest serviceTest;

    @JbootrpcService
    ServiceInter serviceInter;


    public void index() {
        System.out.println("index .... ");
        renderText("hello " + serviceTest.getName());

    }


    @Singleton
    public static class ServiceTest extends JbootService {


        public String getName() {
            return "michael";
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
