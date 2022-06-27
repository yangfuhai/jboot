package io.jboot.test.config;

import io.jboot.app.JbootApplication;

/**
 * test https://gitee.com/JbootProjects/jboot/issues/I592AL
 */
public class TestI592AL {

    public static void main(String[] args) {
        JbootApplication.setBootArg("jboot.config.nacos.enable","true");
        JbootApplication.setBootArg("jboot.config.nacos.serverAddr","${NACOS_SERVER:127.0.0.1:8848}");

        JbootApplication.run(args);
    }
}
