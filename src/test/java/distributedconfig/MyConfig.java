package distributedconfig;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package distributedconfig
 */
@PropertyConfig(prefix = "my")
public class MyConfig {

    private String name = "defalutName";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}