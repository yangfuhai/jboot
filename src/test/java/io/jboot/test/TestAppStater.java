package io.jboot.test;

import io.jboot.app.JbootApplication;

public class TestAppStater {

    public static void main(String[] args){
//        JbootApplication.setBootArg("jboot.app.mode","product");
        JbootApplication.run(args);
    }
}
