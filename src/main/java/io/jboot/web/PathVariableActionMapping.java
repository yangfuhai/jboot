package io.jboot.web;

import com.google.common.base.Joiner;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.config.Routes;
import com.jfinal.core.Action;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import io.jboot.utils.AntPathMatcher;
import io.jboot.utils.ArrayUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PathVariableActionMapping extends JbootActionMapping {
    private static final String PATH_VARIABLE_URL_PATTERN = ".*\\{[a-zA-Z0-9]+\\}.*";
    protected Map<String, Action> pathVariableUrlMapping = new ConcurrentHashMap<>();
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public PathVariableActionMapping(Routes routes) {
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
                        if (actionKey.matches(PATH_VARIABLE_URL_PATTERN)) {
                            Action pathVariableAction = new Action(controllerPath, actionKey, controllerClass, method, methodName, actionInters,
                                    route.getFinalViewPath(routes.getBaseViewPath()));
                            pathVariableUrlMapping.put(actionKey, pathVariableAction);
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




    /**
     * Support four types of url
     * 1: http://abc.com/controllerPath                 ---> 00
     * 2: http://abc.com/controllerPath/para            ---> 01
     * 3: http://abc.com/controllerPath/method          ---> 10
     * 4: http://abc.com/controllerPath/method/para     ---> 11
     * 5: http://abc.com/foo/{id}/bar/{name}
     * The controllerPath can also contains "/"
     * Example: http://abc.com/uvw/xyz/method/para
     */
    @Override
    public Action getAction(String url, String[] urlPara) {
        Action action = mapping.get(url);
        if (action != null) {
            return action;
        }
        for (String pattern : pathVariableUrlMapping.keySet()) {
            //判断是否有匹配包含路径参数的URL映射
            if (antPathMatcher.match(pattern, url)) {
                Action pathVariableUrlAction = pathVariableUrlMapping.get(pattern);
                Map<String, String> pathVariableValues = antPathMatcher.extractUriTemplateVariables(pattern, url);
                urlPara[0] = null;
                if (urlPara.length > 1) {
                    //urlPara[1]作为路径参数传入controller
                    urlPara[1] = Joiner.on("&").withKeyValueSeparator("=").join(pathVariableValues);
                }
                return pathVariableUrlAction;
            }
        }
        // --------
        int i = url.lastIndexOf('/');
        if (i != -1) {
            action = mapping.get(url.substring(0, i));
            if (action != null) {
                urlPara[0] = url.substring(i + 1);
            }
        }

        return action;
    }
}
