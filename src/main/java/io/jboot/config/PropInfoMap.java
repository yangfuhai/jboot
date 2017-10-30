package io.jboot.config;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Prop;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.config
 */
public class PropInfoMap extends ConcurrentHashMap<String, PropInfoMap.PropInfo> {

    public static PropInfoMap create(String key, PropInfo propInfo) {
        PropInfoMap propInfos = new PropInfoMap();
        propInfos.put(key, propInfo);
        return propInfos;
    }

    public PropInfoMap add(String key, PropInfo propInfo) {
        put(key, propInfo);
        return this;
    }

    @Override
    public PropInfo put(String key, PropInfo value) {
        if (containsKey(key)) {
            remove(key);
        }
        return super.put(key, value);
    }


    /**
     * 配置文件的信息 以及 其版本号
     * 只要本地文件发送变化，版本号就不一样
     */
    public static class PropInfo implements Serializable {

        private String version;
        private Properties properties;

        public PropInfo() {
        }

        public PropInfo(String version, Properties properties) {
            this.version = version;
            this.properties = properties;
        }

        public PropInfo(File file) {
            version = HashKit.md5("time:" + file.lastModified() + "-length:" + file.length());
            properties = new Prop(file).getProperties();
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public String getString(Object key) {
            return (String) getProperties().get(key);
        }
    }
}
