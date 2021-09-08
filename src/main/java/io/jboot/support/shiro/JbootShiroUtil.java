package io.jboot.support.shiro;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootController;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootShiroUtil {

    private static final String SLASH = "/";

    /**
     * 参考ActionMapping中的实现。
     *
     * @param controllerClass
     * @param method
     * @param controllerKey
     * @return
     */
    public static String createActionKey(Class<? extends Controller> controllerClass,
                                         Method method, String controllerKey) {
        String methodName = method.getName();
        String actionKey;

        ActionKey ak = method.getAnnotation(ActionKey.class);
        if (ak != null) {
            actionKey = ak.value().trim();
            if ("".equals(actionKey)) {
                throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");
            }
            if (!actionKey.startsWith(SLASH)) {
                actionKey = SLASH + actionKey;
            }
        } else if (methodName.equals("index")) {
            actionKey = controllerKey;
        } else {
            actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;
        }
        return actionKey;
    }

    private static final Set<String> excludedMethodName = new HashSet<String>();

    public static Set<String> getControllerExcludedMethodName() {
        if (excludedMethodName.isEmpty()) {
            Method[] methods = JbootController.class.getMethods();
            for (Method m : methods) {
                excludedMethodName.add(m.getName());
            }
        }

        return excludedMethodName;
    }

}
