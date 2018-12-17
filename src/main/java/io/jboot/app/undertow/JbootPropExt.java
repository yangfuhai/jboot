package io.jboot.app.undertow;

import com.jfinal.server.undertow.PropExt;
import io.jboot.app.config.JbootConfigManager;


public class JbootPropExt extends PropExt {

    public JbootPropExt() {
        super();
        properties.putAll(JbootConfigManager.me().getProperties());
    }
}
