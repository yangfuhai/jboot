package distributedconfig;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package distributedconfig
 */
@PropertyConfig(prefix = "my") //多配置文件使用时 prefix一定做好区分 避免配置项混乱（JbootConfigManager的修改还未做到按应用名进行配置隔离）
public class MyConfig {

    private String name = "defalutName";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}