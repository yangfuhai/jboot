/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.shiro;

import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.component.shiro.error.ShiroErrorProcess;
import io.jboot.component.shiro.processer.*;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassKits;
import io.jboot.web.utils.ControllerUtils;
import org.apache.shiro.authz.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * shiro 管理器.
 */
public class JbootShiroManager {
    private static JbootShiroManager me = new JbootShiroManager();

    private JbootShiroConfig jbootShiroConfig = Jboot.config(JbootShiroConfig.class);

    private JbootShiroManager() {
    }


    public static JbootShiroManager me() {
        return me;
    }

    private ConcurrentHashMap<String, ShiroAuthorizeProcesserInvoker> invokers = new ConcurrentHashMap<>();

    private ShiroRequiresAuthenticationProcesser requiresAuthenticationProcesser = new ShiroRequiresAuthenticationProcesser();
    private ShiroRequiresUserProcesser requiresUserProcesser = new ShiroRequiresUserProcesser();
    private ShiroRequiresGuestProcesser requiresGuestProcesser = new ShiroRequiresGuestProcesser();


    public void init(List<Routes.Route> routes) {
        initInvokers(routes);
    }

    /**
     * 初始化 invokers 变量
     */
    private void initInvokers(List<Routes.Route> routes) {
        Set<String> excludedMethodName = ControllerUtils.buildExcludedMethodName();

        for (Routes.Route route : routes) {
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


                String actionKey = ControllerUtils.createActionKey(controllerClass, method, controllerKey);
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



    public AuthorizeResult invoke(String actionKey) {
        ShiroAuthorizeProcesserInvoker invoker = invokers.get(actionKey);
        if (invoker == null) {
            return AuthorizeResult.ok();
        }

        return invoker.invoke();
    }

    private SsoShiroBridge ssoShiroBridge;

    public SsoShiroBridge getSsoShiroBridge() {
        if (ssoShiroBridge == null && jbootShiroConfig.getSsoShiroBridge() != null) {
            ssoShiroBridge = ClassKits.newInstance(jbootShiroConfig.getSsoShiroBridge());

            if (ssoShiroBridge == null) {
                throw new JbootIllegalConfigException("can not find Class : " + jbootShiroConfig.getSsoShiroBridge() +
                        " please config jboot.shiro.ssoShiroBridge correct. ");
            }
        }

        return ssoShiroBridge;
    }

    private ShiroErrorProcess shiroErrorProcess;

    public ShiroErrorProcess getShiroErrorProcess() {
        if (shiroErrorProcess == null && jbootShiroConfig.getErrorProcess() != null) {
            shiroErrorProcess = ClassKits.newInstance(jbootShiroConfig.getErrorProcess());

            if (shiroErrorProcess == null) {
                throw new JbootIllegalConfigException("can not find Class : " + jbootShiroConfig.getErrorProcess() +
                        " please config jboot.shiro.errorProcess correct. ");
            }
        }

        return shiroErrorProcess;
    }
}
