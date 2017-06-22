import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import io.jboot.db.dao.JbootDaoBase;
import io.jboot.db.model.JbootModel;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


@RequestMapping("/test")
public class ControllerTest extends JbootController {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.setBootArg("jboot.cache.type", "redis");
        Jboot.setBootArg("jboot.cache.redis.host", "127.0.0.1");
        Jboot.run(args);
    }


    @Inject
    @EnableHystrixCommand
    ServiceTest serviceTest;

//    @JbootrpcService
//    ServiceInter serviceInter;


    public void index() {



        List<Record> records = Db.find("select * from `user`");

        System.out.println("index .... ");

        LogKit.error("xxxxxxx");

        Jboot.getCache().put("test","test","valueeeeeeeeee");
        String value = Jboot.getCache().get("test","test");

        System.out.println("value:"+value);


        renderText("hello " + serviceTest.getName());

//        render();




    }


    @Singleton
    public static class ServiceTest extends JbootDaoBase {


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
