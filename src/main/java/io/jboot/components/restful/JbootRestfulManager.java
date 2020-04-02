package io.jboot.components.restful;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import io.jboot.components.restful.annotation.DeleteMapping;
import io.jboot.components.restful.annotation.GetMapping;
import io.jboot.components.restful.annotation.PostMapping;
import io.jboot.components.restful.annotation.PutMapping;
import io.jboot.components.restful.exception.RequestMethodErrorException;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JbootRestfulManager {

    public static class Config {
        private boolean mappingSupperClass;
        private String baseViewPath;
        private Interceptor[] routeInterceptors;
        private List<Routes.Route> routes;

        public Config() {
        }

        public Config(boolean mappingSupperClass, String baseViewPath,
                      Interceptor[] routeInterceptors, List<Routes.Route> routes) {
            this.mappingSupperClass = mappingSupperClass;
            this.baseViewPath = baseViewPath;
            this.routeInterceptors = routeInterceptors;
            this.routes = routes;
        }

        public boolean isMappingSupperClass() {
            return mappingSupperClass;
        }

        public Config setMappingSupperClass(boolean mappingSupperClass) {
            this.mappingSupperClass = mappingSupperClass;
            return this;
        }

        public String getBaseViewPath() {
            return baseViewPath;
        }

        public Config setBaseViewPath(String baseViewPath) {
            this.baseViewPath = baseViewPath;
            return this;
        }

        public Interceptor[] getRouteInterceptors() {
            return routeInterceptors;
        }

        public Config setRouteInterceptors(Interceptor[] routeInterceptors) {
            this.routeInterceptors = routeInterceptors;
            return this;
        }

        public List<Routes.Route> getRoutes() {
            return routes;
        }

        public Config setRoutes(List<Routes.Route> routes) {
            this.routes = routes;
            return this;
        }
    }

    private static JbootRestfulManager me = new JbootRestfulManager();

    private Map<String, RestfulAction> restfulActions = new HashMap<>(2048, 0.5F);

    protected static final String SLASH = "/";

    private RestfulErrorRender restfulErrorRender = new DefaultRestfulErrorRender();

    public static JbootRestfulManager me() {
        return me;
    }

    public void init(Config config) {
        if (config.getRoutes() == null || config.getRoutes().isEmpty()) {
            return;
        }
        InterceptorManager interMan = InterceptorManager.me();
        Class<?> dc;
        // 初始化自定义的restful controller
        for (Routes.Route route : config.getRoutes()) {
            Class<? extends Controller> controllerClass = route.getControllerClass();
            Interceptor[] controllerInters = interMan.createControllerInterceptor(controllerClass);

            boolean declaredMethods = config.isMappingSupperClass()
                    ? controllerClass.getSuperclass() == Controller.class
                    : true;

            String baseRequestMapping = SLASH;
            if (controllerClass.getAnnotation(RequestMapping.class) != null) {
                RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
                if (requestMapping.value().startsWith(SLASH)) {
                    baseRequestMapping = requestMapping.value();
                } else {
                    baseRequestMapping = baseRequestMapping + requestMapping.value();
                }
            }

            Method[] methods = (declaredMethods ? controllerClass.getDeclaredMethods() : controllerClass.getMethods());
            for (Method method : methods) {
                if (declaredMethods) {
                    if (!Modifier.isPublic(method.getModifiers())) {
                        continue;
                    }
                } else {
                    dc = method.getDeclaringClass();
                    if (dc == Controller.class || dc == Object.class) {
                        continue;
                    }
                }
                //去除mapping
                if (method.getAnnotation(NotAction.class) != null) {
                    continue;
                }
                Interceptor[] actionInters = interMan.buildControllerActionInterceptor(config.getRouteInterceptors(), controllerInters, controllerClass, method);

                String actionKey = baseRequestMapping;

                //GET 判断
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                PutMapping putMapping = method.getAnnotation(PutMapping.class);
                DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                String requestMethod = "", mappingValue = "";
                if (getMapping != null) {
                    requestMethod = "GET";
                    if (StrUtil.isNotBlank(getMapping.value())) {
                        mappingValue = getMapping.value();
                    }
                } else if (postMapping != null) {
                    requestMethod = "POST";
                    if (StrUtil.isNotBlank(postMapping.value())) {
                        mappingValue = postMapping.value();
                    }
                } else if (putMapping != null) {
                    requestMethod = "PUT";
                    if (StrUtil.isNotBlank(putMapping.value())) {
                        mappingValue = putMapping.value();
                    }
                } else if (deleteMapping != null) {
                    requestMethod = "DELETE";
                    if (StrUtil.isNotBlank(deleteMapping.value())) {
                        mappingValue = deleteMapping.value();
                    }
                } else {
                    //默认为get请求
                    requestMethod = "GET";
                    mappingValue = SLASH;
                }
                if (StrUtil.isNotBlank(mappingValue)) {
                    if (!actionKey.endsWith(SLASH)) {
                        actionKey = actionKey + SLASH;
                    }
                    if (mappingValue.startsWith(SLASH)) {
                        mappingValue = mappingValue.substring(1);
                    }
                    actionKey = actionKey + mappingValue;
                } else {
                    if (actionKey.endsWith(SLASH)) {
                        actionKey = actionKey.substring(0, actionKey.length() - 1);
                    }
                }
                RestfulAction action = new RestfulAction(baseRequestMapping, actionKey, controllerClass,
                        method, method.getName(), actionInters, route.getFinalViewPath(config.getBaseViewPath()));
                String key = requestMethod + ":" + actionKey;

//                RestfulAction restfulAction = new RestfulAction(action, actionKey, requestMethod);
                if (restfulActions.put(key, action) != null) {
                    //已经存在指定的key
                    throw new RuntimeException(buildMsg(actionKey, controllerClass, method));
                }
            }
        }
    }

    protected String buildMsg(String actionKey, Class<? extends Controller> controllerClass, Method method) {
        StringBuilder sb = new StringBuilder("The action \"")
                .append(controllerClass.getName()).append(".")
                .append(method.getName()).append("()\" can not be mapped, ")
                .append("actionKey \"").append(actionKey).append("\" is already in use.");

        String msg = sb.toString();
        System.err.println("\nException: " + msg);
        return msg;
    }

    public RestfulAction getRestfulAction(String target, String requestMethod) {
        String actionKey = requestMethod + ":" + target;
        //先直接获取
        RestfulAction restfulAction = restfulActions.get(actionKey);
        if (restfulAction == null) {
            //路径判断
            String[] paths = actionKey.split(":")[1].replace(requestMethod, "").split(SLASH);
            for (String _actionKey : restfulActions.keySet()) {
                String _requestMethod = _actionKey.split(":")[0];
                String _target = _actionKey.split(":")[1];
                System.out.println("---------> target:"+target+",_target:"+_target+",_requestMethod:"+_requestMethod+",requestMethod:" + requestMethod);
                if( target.equals(_target) && !_requestMethod.equals(requestMethod) ){
                    //请求方法不正确
                    throw new RequestMethodErrorException(_actionKey, _requestMethod, target, requestMethod);
                }
                String[] _paths = _actionKey.split(":")[1].replace(requestMethod, "").split(SLASH);
                if (_actionKey.startsWith(requestMethod) &&
                        _actionKey.contains("{") && _actionKey.contains("}")
                        && paths.length == _paths.length && RestfulUtils.comparePaths(_paths, paths)) {
                    restfulAction = restfulActions.get(_actionKey);
                    break;
                }
            }
        }
        return restfulAction;
    }

    public RestfulErrorRender getRestfulErrorRender() {
        return restfulErrorRender;
    }

    public JbootRestfulManager setRestfulErrorRender(RestfulErrorRender restfulErrorRender) {
        this.restfulErrorRender = restfulErrorRender;
        return this;
    }
}
