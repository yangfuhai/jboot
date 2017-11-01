package io.jboot.web;

import com.jfinal.core.Controller;
import com.jfinal.core.ControllerFactory;
import io.jboot.Jboot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web
 */
public class JbootControllerFactory extends ControllerFactory {

    private ThreadLocal<Map<Class<? extends Controller>, Controller>> buffers = new ThreadLocal<Map<Class<? extends Controller>, Controller>>() {
        protected Map<Class<? extends Controller>, Controller> initialValue() {
            return new HashMap<Class<? extends Controller>, Controller>();
        }
    };

    public Controller getController(Class<? extends Controller> controllerClass) throws InstantiationException, IllegalAccessException {
        Controller ret = buffers.get().get(controllerClass);
        if (ret == null) {
            ret = controllerClass.newInstance();
            Jboot.injectMembers(ret);
            buffers.get().put(controllerClass, ret);
        }
        return ret;
    }
}