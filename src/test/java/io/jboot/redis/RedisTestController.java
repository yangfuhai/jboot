package io.jboot.redis;

import io.jboot.Jboot;
import io.jboot.kits.StringKits;
import io.jboot.support.redis.JbootRedis;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/redis")
public class RedisTestController extends JbootController {

    public void index() {
        String html = "<a href=\"/redis/show\"> show </a> <br>\n" +
                "<a href=\"/redis/put\"> put </a> <br>\n" +
                "<a href=\"/redis/del\"> del </a> <br>";

        renderHtml(html);
    }

    public void show() {

        String key = getPara("key");
        if (StringKits.isBlank(key)) {
            renderText("key is empty. ");
            return;
        }

        JbootRedis redis = Jboot.getRedis();
        if (redis == null) {
            renderText("can not get redis, maybe redis config is error.");
            return;
        }

        Object object = redis.get(key);
        renderText("value : " + object);

    }

    public void set() {

        String key = getPara("key");
        if (StringKits.isBlank(key)) {
            renderText("key is empty. ");
            return;
        }

        String value = getPara("value");
        if (StringKits.isBlank(value)) {
            renderText("value is empty. ");
            return;
        }

        JbootRedis redis = Jboot.getRedis();
        if (redis == null) {
            renderText("can not get redis, maybe redis config is error.");
            return;
        }

        redis.set(key,value);
        renderText("set ok " );
    }


    public void del() {

        String key = getPara("key");
        if (StringKits.isBlank(key)) {
            renderText("key is empty. ");
            return;
        }

        JbootRedis redis = Jboot.getRedis();
        if (redis == null) {
            renderText("can not get redis, maybe redis config is error.");
            return;
        }

        redis.del(key);
        renderText("del ok " );

    }
}
