package swagger;

import io.jboot.Jboot;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @package config
 */
public class ConfigClient {


    /**
     * 启动后，通过 http://127.0.0.1:8080/jboot/config 来查看数据
     *
     * @param args
     */
    public static void main(String[] args) {

        /**
         *  private String title;
         private String description;
         private String version;
         private String termsOfService;
         */

        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        Jboot.setBootArg("jboot.swagger.url", "/swagger");
        Jboot.setBootArg("jboot.swagger.title", "Jboot API 测试");
        Jboot.setBootArg("jboot.swagger.description", "这真的真的真的只是一个测试而已，不要当真。");
        Jboot.setBootArg("jboot.swagger.version", "1.0");
        Jboot.setBootArg("jboot.swagger.termsOfService", "http://jboot.io");
        Jboot.setBootArg("jboot.swagger.contact", "email:fuhai999@gmail.com;qq:123456");
        Jboot.setBootArg("jboot.swagger.host", "http://127.0.0.1:8088");

        Jboot.run(args);


    }

}
