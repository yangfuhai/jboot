package io.jboot.app.undertow;

import com.jfinal.server.undertow.PropExt;
import com.jfinal.server.undertow.UndertowConfig;
import io.jboot.app.config.JbootConfigManager;

public class JbootUndertowConfig extends UndertowConfig {

    public JbootUndertowConfig(Class<?> jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JbootUndertowConfig(String jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JbootUndertowConfig(Class<?> jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    public JbootUndertowConfig(String jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    @Override
    protected PropExt createPropExt(String undertowConfig) {
        return super.createPropExt(undertowConfig)
                .append(new PropExt(JbootConfigManager.me().getProperties()));
    }
}

