package sharding;

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */
@RequestMapping("/sharding")
public class ShardingController extends JbootController {


    public void index() {

        UserModel user = new UserModel();
        user.setId(StringUtils.uuid());
        user.setName("Michael yang");

        user.save();

        renderText("插入数据成功，请查看数据库...");

    }


    public static void main(String[] args) {


        Jboot.setBootArg("jboot.datasource.type", "mysql");
        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootsharding");
        Jboot.setBootArg("jboot.datasource.user", "root");
        Jboot.setBootArg("jboot.datasource.password", "");
        Jboot.setBootArg("jboot.datasource.shardingEnable", "true");


        Jboot.run(args);
    }
}
