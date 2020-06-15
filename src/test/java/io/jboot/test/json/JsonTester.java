package io.jboot.test.json;

import io.jboot.test.db.model.User;
import io.jboot.web.JbootJson;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JsonTester {

    public static void main(String[] args) {
        User user = new User();
        user.put("id",100);
        user.put("tenant_id","xxx");

        user.put("other_user",new User());

        System.out.println(new JbootJson().toJson(user));
    }
}
