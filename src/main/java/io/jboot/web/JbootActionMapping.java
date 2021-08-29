package io.jboot.web;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.config.Routes;
import com.jfinal.core.*;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassUtil;

import java.lang.reflect.*;

public class JbootActionMapping extends ActionMapping {

    public JbootActionMapping(Routes routes) {
        super(routes);
    }

    @Override
    protected void buildActionMapping() {
        mapping.clear();
        Class<?> dc;
        InterceptorManager interMan = InterceptorManager.me();
        for (Routes routes : getRoutesList()) {
            for (Routes.Route route : routes.getRouteItemList()) {
                Class<? extends Controller> controllerClass = route.getControllerClass();
                Interceptor[] controllerInters = interMan.createControllerInterceptor(controllerClass);

                boolean declaredMethods = !routes.getMappingSuperClass() || controllerClass.getSuperclass() == Controller.class;

                Method[] methods = (declaredMethods ? controllerClass.getDeclaredMethods() : controllerClass.getMethods());
                for (Method method : methods) {
                    if (declaredMethods) {
                        if (!Modifier.isPublic(method.getModifiers()))
                            continue;
                    } else {
                        dc = method.getDeclaringClass();
                        if (dc == Controller.class || dc == Object.class)
                            continue;
                    }

                    if (method.getAnnotation(NotAction.class) != null) {
                        continue;
                    }

                    Interceptor[] actionInters = interMan.buildControllerActionInterceptor(routes.getInterceptors(), controllerInters, controllerClass, method);
                    String controllerPath = route.getControllerPath();

                    String methodName = method.getName();
                    ActionKey ak = method.getAnnotation(ActionKey.class);
                    String actionKey;
                    if (ak != null) {
                        actionKey = ak.value().trim();
                        if ("".equals(actionKey)) {
                            throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");
                        }

                        if (actionKey.startsWith(SLASH)) {
                            //actionKey = actionKey
                        } else if (actionKey.startsWith("./")) {
                            actionKey = controllerPath + actionKey.substring(1);
                        } else {
                            actionKey = SLASH + actionKey;
                        }
//                        if (!actionKey.startsWith(SLASH)) {
//                            actionKey = SLASH + actionKey;
//                        }
                    } else if (methodName.equals("index")) {
                        actionKey = controllerPath;
                    } else {
                        actionKey = controllerPath.equals(SLASH) ? SLASH + methodName : controllerPath + SLASH + methodName;
                    }

//                    Action action = new Action(controllerPath, actionKey, controllerClass, method, methodName, actionInters, route.getFinalViewPath(routes.getBaseViewPath()));
//                    if (mapping.put(actionKey, action) != null) {
//                        throw new RuntimeException(buildMsg(actionKey, controllerClass, method));
//                    }

                    Action newAction = new Action(controllerPath, actionKey, controllerClass, method, methodName, actionInters, route.getFinalViewPath(routes.getBaseViewPath()));
                    Action existAction = mapping.get(actionKey);
                    if (existAction == null) {
                        mapping.put(actionKey, newAction);
                    } else {

                        Type controllerType = controllerClass.getGenericSuperclass();
                        Method existActionMethod = existAction.getMethod();

                        // 不是泛型
                        if (!(controllerType instanceof ParameterizedType)) {
                            throw new RuntimeException(buildMsg(actionKey, method, existActionMethod));
                        }

                        if (method.getParameterCount() == 0
                                || method.getParameterCount() != existActionMethod.getParameterCount()
                                || method.getDeclaringClass() != existActionMethod.getDeclaringClass()) {
                            throw new RuntimeException(buildMsg(actionKey, method, existActionMethod));
                        }

                        Type[] argumentTypes = ((ParameterizedType) controllerType).getActualTypeArguments();

                        Class<?>[] paraTypes = method.getParameterTypes();
                        Class<?>[] existParaTypes = existActionMethod.getParameterTypes();

                        for (int i = 0; i < paraTypes.length; i++) {
                            Class<?> newType = paraTypes[i];
                            Class<?> existType = existParaTypes[i];
                            if (newType == existType) {
                                continue;
                            }
                            // newType 是父类
                            else if (newType.isAssignableFrom(existType) && ArrayUtil.contains(argumentTypes, existType)) {
                                break;
                            }
                            // newType 是子类
                            else if (existType.isAssignableFrom(newType) && ArrayUtil.contains(argumentTypes, newType)) {
                                mapping.put(actionKey, newAction);
                                break;
                            } else {
                                throw new RuntimeException(buildMsg(actionKey, method, existActionMethod));
                            }
                        }

                    }
                }
            }
        }
        routes.clear();

        // support url = controllerPath + urlParas with "/" of controllerPath
        Action action = mapping.get("/");
        if (action != null) {
            mapping.put("", action);
        }
    }


    protected String buildMsg(String actionKey, Method method, Method existMethod) {
        StringBuilder sb = new StringBuilder("The action \"")
                .append(ClassUtil.buildMethodString(method))
                .append("\" can not be mapped, actionKey \"")
                .append(actionKey)
                .append("\" is already in used by: \"")
                .append(ClassUtil.buildMethodString(existMethod))
                .append("\"");

        String msg = sb.toString();
        System.err.println("\nException: " + msg);
        return msg;
    }


}
