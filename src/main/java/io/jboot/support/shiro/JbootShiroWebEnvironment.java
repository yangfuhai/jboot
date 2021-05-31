package io.jboot.support.shiro;

import io.jboot.Jboot;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.web.env.IniWebEnvironment;

public class JbootShiroWebEnvironment extends IniWebEnvironment {
    @Override
    protected String[] getDefaultConfigLocations() {
        //读取jboot配置文件中的jboot.shiro.ini配置项
        String iniFileName = "classpath:" + Jboot.configValue("jboot.shiro.ini", "shiro.ini");
        return new String[]{
                iniFileName,
                DEFAULT_WEB_INI_RESOURCE_PATH,
                IniFactorySupport.DEFAULT_INI_RESOURCE_PATH
        };
    }
}
