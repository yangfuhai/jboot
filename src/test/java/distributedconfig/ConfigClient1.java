package distributedconfig;

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


        //jboot端口号配置
        Jboot.setBootArg("jboot.server.port", "8088");

        Jboot.setBootArg("jboot.config.remoteEnable", "true");
        Jboot.setBootArg("jboot.config.remoteUrl", "http://127.0.0.1:8080/jboot/config");
        //Jboot.setBootArg("jboot.config.appName", "mos")

        MyConfig config = Jboot.config(MyConfig.class);

        for (int i = 0; i < 1000000; i++) {
            System.out.println("------myname:" + config.getName());
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Jboot.run(args);
    }


}
