package distributedconfig;

import io.jboot.Jboot;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package config
 */
public class ConfigServer {


    /**
     * 启动后，通过 http://127.0.0.1:8080/jboot/config 来查看数据
     *
     * @param args
     */
    public static void main(String[] args) {

        Jboot.setBootArg("jboot.config.serverEnable", "true");
        //Jboot.setBootArg("jboot.config.path", "/Users/michael/Desktop/test");
        Jboot.setBootArg("jboot.config.path", "C://config");//config在resources目录下 复制到C盘 即可

        Jboot.run(args);
    }
}