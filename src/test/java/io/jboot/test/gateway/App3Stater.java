package io.jboot.test.gateway;

import io.jboot.app.JbootApplication;

public class App3Stater {

    public static void main(String[] args){
        JbootApplication.setBootArg("undertow.port",9903);
        JbootApplication.setBootArg("jboot.gateway.enable ",false);
        JbootApplication.run(args);
    }
}
