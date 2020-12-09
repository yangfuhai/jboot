package io.jboot.web.para;

import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.ParaGetter;

import java.util.Map;

public class MapParaGetter extends  ParaGetter<Map> {


    public MapParaGetter(String parameterName, String defaultValue) {
        super(parameterName, defaultValue);
    }

    @Override
    protected Map to(String v) {
        return null;
    }

    @Override
    public Map get(Action action, Controller c) {
        return null;
    }
}

