package io.jboot.app.undertow;

import com.jfinal.server.undertow.UndertowConfig;

public class JbootUndertowConfig extends UndertowConfig {

    public JbootUndertowConfig(Class<?> jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JbootUndertowConfig(String jfinalConfigClass) {
        super(jfinalConfigClass);
        p.append(new JbootPropExt());
        init();
    }

    public JbootUndertowConfig(Class<?> jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    public JbootUndertowConfig(String jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
        p.append(new JbootPropExt());
        init();
    }
}

