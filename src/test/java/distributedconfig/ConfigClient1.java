package distributedconfig;

import io.jboot.Jboot;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @package config
 */
public class ConfigClient1 {


    /**
     * 启动后，通过 http://127.0.0.1:8080/jboot/config 来查看数据
     *
     * @param args
     */
    public static void main(String[] args) {


        //jboot端口号配置
        //Jboot.setBootArg("jboot.server.port", "8087");

        Jboot.setBootArg("jboot.config.remoteEnable", "true");
        Jboot.setBootArg("jboot.config.remoteUrl", "http://127.0.0.1:8080/jboot/config");
        Jboot.setBootArg("jboot.config.appName", "mos");

        Jboot.run(args);

        MyConfig1 config = Jboot.config(MyConfig1.class);

        for (int i = 0; i < 1000000; i++) {
            System.out.println("------myname1:" + config.getName());
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
