package io.jboot.test.jfinal;

import io.jboot.Jboot;
import io.jboot.web.directive.annotation.JFinalSharedEnum;

import java.util.Arrays;

@JFinalSharedEnum
public enum JfinalEnum {

    MASTER(1, "总部"),

    AGENT(10, "代理商"),

    CLIENT(20, "SaaS租户"),

    DEV(99, "开发者");


    private int value;
    private String text;

    JfinalEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static JfinalEnum get(Integer value) {
        if (value != null) {
            for (JfinalEnum type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
        }
        return null;
    }


    public static boolean isDev(Integer value, Object r) throws InterruptedException {
        Thread.sleep(2000);
        return true;
    }

    public static boolean isDev(Integer value) {
        // 只有在开发模式下，isDev 才会生效
        return Jboot.isDevMode() && value != null && value == DEV.value;
    }


    public static boolean isMaster(Integer value) {
        return value != null && value == MASTER.value;
    }


    public static boolean isAgent(Integer value) {
        return value != null && value == AGENT.value;
    }


    public static boolean isClient(Integer value) {
        return value != null && value == CLIENT.value;
    }

    public static void main(String[] args) {
        Object[] xxxs= values();
        System.out.println(Arrays.toString(xxxs));
    }

    public static String str(){
        return "hello";
    }


}
