package io.jboot.test.aop.staticconstruct;

import io.jboot.aop.annotation.StaticConstruct;

@StaticConstruct
public class StaticConstructManager {

    private static StaticConstructManager me = new StaticConstructManager();

    private StaticConstructManager(){
        System.out.println("StaticConstructManager() invoked");
    }

    public static StaticConstructManager me(){
        System.out.println("StaticConstructManager.me() invoked");
        return me;
    }
}
