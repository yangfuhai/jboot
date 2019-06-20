package io.jboot.test.swagger;

import io.jboot.app.JbootApplication;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: swagger启动 以及配置
 */
public class SwaggerStarter {

    /**
     * 启动后，访问：http://127.0.0.1:8080/swaggerui
     *
     * @param args
     */
    public static void main(String[] args) {


        //jboot端口号配置
        JbootApplication.setBootArg("undertow.resourcePath", "src/test/webapp");

        JbootApplication.setBootArg("jboot.swagger.path", "/swaggerui");
        JbootApplication.setBootArg("jboot.swagger.title", "Jboot API 测试");
        JbootApplication.setBootArg("jboot.swagger.description", "这是一个Jboot对Swagger支持的测试demo。");
        JbootApplication.setBootArg("jboot.swagger.version", "1.0");
        JbootApplication.setBootArg("jboot.swagger.termsOfService", "http://jboot.io");
        JbootApplication.setBootArg("jboot.swagger.contactEmail", "fuhai999@gmail.com");
        JbootApplication.setBootArg("jboot.swagger.contactName", "fuhai999");
        JbootApplication.setBootArg("jboot.swagger.contactUrl", "http://jboot.io");
        JbootApplication.setBootArg("jboot.swagger.host", "127.0.0.1:8080");

        JbootApplication.run(args);


    }
}
