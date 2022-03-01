package io.jboot.test.json;

import com.jfinal.core.Controller;
import io.jboot.app.JbootApplication;
import io.jboot.test.db.model.User;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/json")
public class JsonRenderController extends Controller {

    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.json.camelCaseJsonStyleEnable", "false");
        JbootApplication.setBootArg("jboot.json.skipBeanGetters", "true");
        JbootApplication.run(args);
    }

    public Object test(){

        User user = new User();
        user.put("user_id",1);
        user.put("type","aaa");
        user.put("type_id",100);

        return user;
    }
}
