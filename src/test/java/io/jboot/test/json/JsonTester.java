package io.jboot.test.json;

import com.alibaba.fastjson.JSON;
import io.jboot.test.db.model.User;
import io.jboot.web.json.JbootJson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JsonTester {

    public static void main(String[] args) {
        User user = new User();
        user.put("id",100);
        user.put("tenant_id","xxx");

        user.put("other_user",new User());
        user.put("myAbcDef",new User());

        System.out.println(new JbootJson().toJson(user));


        Map map = new HashMap();
        map.put("zhangsan",100);
        map.put("lisi",200);
        map.put("wangyu",300);
        map.put("zhao_liu",300);
        map.put("self",map);

        System.out.println(new JbootJson().toJson(map));
        System.out.println(JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(user));
    }
}
