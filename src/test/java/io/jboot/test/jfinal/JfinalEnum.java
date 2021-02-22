package io.jboot.test.jfinal;

import io.jboot.Jboot;
import io.jboot.web.directive.annotation.JFinalSharedEnum;

@JFinalSharedEnum
public enum JfinalEnum {

    /**
     * 总部一般为系统拥有者
     */
    MASTER(1, "总部"),

    /**
     * 代理商为 总部的代理商，代理商可以和总部共用一套系统
     */
    AGENT(10, "代理商"),

    /**
     * SaaS 租户一般为门店客户
     */
    CLIENT(20, "SaaS租户"),

    /**
     * CONSUMER 客户，一般是 SaaS 租户发展的消费者
     */
    CONSUMER(30, "消费者"),

    /**
     * 用户，一般在非 SaaS 中使用，可以登录后台的是 master，其他统称 user
     */
    USER(40, "用户"),

    /**
     * 开发者，拥有所有权限，方便开发或者系统护卫使用，只用于在开发的时候，也就是配置 jboot.app.mode = dev 的时候允许登录
     */
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



    public static boolean isDev(Integer value) {
        // 只有在开发模式下，isDev 才会生效
        return Jboot.isDevMode() && value != null && value == DEV.value;
    }


    /**
     * 是否是总部员工
     *
     * @param value
     * @return
     */
    public static boolean isMaster(Integer value) {
        return value != null && value == MASTER.value;
    }


    /**
     * 是否是代理商 或者 代理商员工账号
     *
     * @param value
     * @return
     */
    public static boolean isAgent(Integer value) {
        return value != null && value == AGENT.value;
    }



    /**
     * 是否是租户（门店客户），或者租户的员工账号
     * @param value
     * @return
     */
    public static boolean isClient(Integer value) {
        return value != null && value == CLIENT.value;
    }



    /**
     * 是否是客户，一般是 SaaS 租户发展的消费者
     *
     * @param value
     * @return
     */
    public static boolean isConsumer(Integer value) {
        return value != null && value == CONSUMER.value;
    }


    /**
     * 非 SaaS 系统里，一般只有 master 和 user
     *
     * @param value
     * @return
     */
    public static boolean isUser(Integer value) {
        return value != null && value == USER.value;
    }
}
