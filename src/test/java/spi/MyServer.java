package spi;

import io.jboot.core.spi.JbootSpi;
import io.jboot.server.JbootServer;
import io.jboot.server.undertow.UnderTowServer;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package spi
 */
@JbootSpi("myserver")
public class MyServer extends UnderTowServer  implements JbootServer {

    public MyServer(){
        System.out.println("new MyServer");
    }
}
