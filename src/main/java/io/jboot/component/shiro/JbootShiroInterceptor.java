package io.jboot.component.shiro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.util.ThreadContext;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.Routes;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

import io.jboot.Jboot;
import io.jboot.component.shiro.processer.AuthorizeResult;
import io.jboot.component.shiro.processer.ShiroClear;
import io.jboot.component.shiro.processer.ShiroRequiresAuthenticationProcesser;
import io.jboot.component.shiro.processer.ShiroRequiresGuestProcesser;
import io.jboot.component.shiro.processer.ShiroRequiresPermissionsProcesser;
import io.jboot.component.shiro.processer.ShiroRequiresRolesProcesser;
import io.jboot.component.shiro.processer.ShiroRequiresUserProcesser;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.JbootAppConfig;

/**
 * Shiro 拦截器
 */
public class JbootShiroInterceptor implements Interceptor {


    private ConcurrentHashMap<String, ShiroAuthorizeProcesserInvoker> invokers = new ConcurrentHashMap<>();
    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);


    private ShiroRequiresAuthenticationProcesser requiresAuthenticationProcesser = new ShiroRequiresAuthenticationProcesser();
    private ShiroRequiresUserProcesser requiresUserProcesser = new ShiroRequiresUserProcesser();
    private ShiroRequiresGuestProcesser requiresGuestProcesser = new ShiroRequiresGuestProcesser();


    public JbootShiroInterceptor() {
        initInvokers();
    }

    /**
     * 初始化 invokers 变量
     */
    private void initInvokers() {
        Set<String> excludedMethodName = buildExcludedMethodName();

        for (Routes.Route route : JbootAppConfig.routes.getRouteItemList()) {
            Class<? extends Controller> controllerClass = route.getControllerClass();

            String controllerKey = route.getControllerKey();

            Annotation[] controllerAnnotations = controllerClass.getAnnotations();

            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                if (excludedMethodName.contains(method.getName()) || method.getParameterTypes().length != 0) {
                    continue;
                }

                if (method.getAnnotation(ShiroClear.class) != null) {
                    continue;
                }


                Annotation[] methodAnnotations = method.getAnnotations();
                Annotation[] allAnnotations = ArrayUtils.concat(controllerAnnotations, methodAnnotations);


                String actionKey = createActionKey(controllerClass, method, controllerKey);
                ShiroAuthorizeProcesserInvoker invoker = new ShiroAuthorizeProcesserInvoker();


                for (Annotation annotation : allAnnotations) {
                    if (annotation.annotationType() == RequiresPermissions.class) {
                        ShiroRequiresPermissionsProcesser processer = new ShiroRequiresPermissionsProcesser((RequiresPermissions) annotation);
                        invoker.addProcesser(processer);
                    } else if (annotation.annotationType() == RequiresRoles.class) {
                        ShiroRequiresRolesProcesser processer = new ShiroRequiresRolesProcesser((RequiresRoles) annotation);
                        invoker.addProcesser(processer);
                    } else if (annotation.annotationType() == RequiresUser.class) {
                        invoker.addProcesser(requiresUserProcesser);
                    } else if (annotation.annotationType() == RequiresAuthentication.class) {
                        invoker.addProcesser(requiresAuthenticationProcesser);
                    } else if (annotation.annotationType() == RequiresGuest.class) {
                        invoker.addProcesser(requiresGuestProcesser);
                    }
                }

                if (invoker.getProcessers() != null && invoker.getProcessers().size() > 0) {
                    invokers.put(actionKey, invoker);
                }

            }
        }
    }


    private static String SLASH = "/";

    /**
     * 参考ActionMapping中的实现。
     *
     * @param controllerClass
     * @param method
     * @param controllerKey
     * @return
     */

    private String createActionKey(Class<? extends Controller> controllerClass,
                                   Method method, String controllerKey) {
        String methodName = method.getName();
        String actionKey;

        ActionKey ak = method.getAnnotation(ActionKey.class);
        if (ak != null) {
            actionKey = ak.value().trim();
            if ("".equals(actionKey))
                throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");
            if (!actionKey.startsWith(SLASH))
                actionKey = SLASH + actionKey;
        } else if (methodName.equals("index")) {
            actionKey = controllerKey;
        } else {
            actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;
        }
        return actionKey;
    }


    private Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = Controller.class.getMethods();
        for (Method m : methods) {
            if (m.getParameterTypes().length == 0)
                excludedMethodName.add(m.getName());
        }
        return excludedMethodName;
    }


    @Override
    public void intercept(Invocation inv) {
        if (!config.isConfigOK()) {
            inv.invoke();
            return;
        }
        try {
            doIntercept(inv);
        } catch (Exception e) {
            ThreadContext.unbindSubject();
        }

    }

    private void doIntercept(Invocation inv) {
        ShiroAuthorizeProcesserInvoker invoker = invokers.get(inv.getActionKey());
        if (invoker == null) {
            inv.invoke();
            return;
        }

        AuthorizeResult result = invoker.invoke();

        if (result.isOk()) {
            inv.invoke();
            return;
        }

        int errorCode = result.getErrorCode();
        switch (errorCode) {
            case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                doProcessUnauthenticated(inv.getController());
                break;
            case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                doProcessuUnauthorization(inv.getController());
                break;
            default:
                inv.getController().renderError(404);
        }
    }


    /**
     * 未认证处理
     *
     * @param controller
     */
    private void doProcessUnauthenticated(Controller controller) {
        if (StringUtils.isBlank(config.getLoginUrl())) {
            controller.renderError(401);
            return;
        }
        controller.redirect(config.getLoginUrl());
    }


    /**
     * 未授权处理
     *
     * @param controller
     */
    private void doProcessuUnauthorization(Controller controller) {
        if (StringUtils.isBlank(config.getUnauthorizedUrl())) {
            controller.renderError(403);
            return;
        }
        controller.redirect(config.getUnauthorizedUrl());
    }


}
