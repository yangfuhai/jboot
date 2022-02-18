/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.support.shiro;

import com.jfinal.aop.Invocation;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.template.expr.ast.MethodKeyBuilder;
import io.jboot.Jboot;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.support.shiro.processer.*;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;
import org.apache.shiro.authz.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * shiro 管理器.
 */
public class JbootShiroManager {
    private final static JbootShiroManager me = new JbootShiroManager();

    private final JbootShiroConfig jbootShiroConfig = Jboot.config(JbootShiroConfig.class);

    private final ShiroRequiresAuthenticationProcesser requiresAuthenticationProcessor = new ShiroRequiresAuthenticationProcesser();
    private final ShiroRequiresUserProcesser requiresUserProcessor = new ShiroRequiresUserProcesser();
    private final ShiroRequiresGuestProcesser requiresGuestProcessor = new ShiroRequiresGuestProcesser();

    private JbootShiroManager() {
    }


    public static JbootShiroManager me() {
        return me;
    }

    private ConcurrentHashMap<Long, ShiroAuthorizeProcesserInvoker> invokers = new ConcurrentHashMap<>();


    public void init(List<Routes.Route> routes) {
        // do nothing
    }

    /**
     * 根据类和方法上的注解生成shiro的注解处理器
     *
     * @return 返回是否有shiro处理器，ShiroInterceptorBuilder 根据这一结果来决定是否对方法进行拦截
     */
    public boolean buildShiroInvoker(Class clazz, Method method) {
        if (Controller.class.isAssignableFrom(clazz) &&
                JbootShiroUtil.getControllerExcludedMethodName().contains(method.getName())) {
            // 忽略 JbootController 中的方法
            return false;
        }

        if (method.getAnnotation(ShiroClear.class) != null) {
            return false;
        }

        Annotation[] allAnnotations = ArrayUtil.concat(clazz.getAnnotations(), method.getAnnotations());
        ShiroAuthorizeProcesserInvoker invoker = new ShiroAuthorizeProcesserInvoker();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType() == RequiresPermissions.class) {
                ShiroRequiresPermissionsProcesser processor = new ShiroRequiresPermissionsProcesser((RequiresPermissions) annotation);
                invoker.addProcesser(processor);
            } else if (annotation.annotationType() == RequiresRoles.class) {
                ShiroRequiresRolesProcesser processor = new ShiroRequiresRolesProcesser((RequiresRoles) annotation);
                invoker.addProcesser(processor);
            } else if (annotation.annotationType() == RequiresUser.class) {
                invoker.addProcesser(requiresUserProcessor);
            } else if (annotation.annotationType() == RequiresAuthentication.class) {
                invoker.addProcesser(requiresAuthenticationProcessor);
            } else if (annotation.annotationType() == RequiresGuest.class) {
                invoker.addProcesser(requiresGuestProcessor);
            }
        }

        if (invoker.getProcessers() != null && invoker.getProcessers().size() > 0) {
            invokers.put(getMethodKey(method), invoker);
            return true;
        }

        return false;
    }

    public AuthorizeResult invoke(Invocation invocation) {
        ShiroAuthorizeProcesserInvoker invoker = invokers.get(getMethodKey(invocation.getMethod()));
        if (invoker == null) {
            return AuthorizeResult.ok();
        }

        return invoker.invoke();
    }


    private static MethodKeyBuilder keyBuilder = new MethodKeyBuilder.FastMethodKeyBuilder();

    public static Long getMethodKey(Method method) {
        return keyBuilder.getMethodKey(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }


    private JbootShiroInvokeListener invokeListener;

    public JbootShiroInvokeListener getInvokeListener() {

        if (invokeListener != null) {
            return invokeListener;
        }

        invokeListener = JbootShiroInvokeListener.DEFAULT;

        if (StrUtil.isNotBlank(jbootShiroConfig.getInvokeListener())) {
            invokeListener = ClassUtil.newInstance(jbootShiroConfig.getInvokeListener());
            if (invokeListener == null) {
                throw new JbootIllegalConfigException("can not find Class : " + jbootShiroConfig.getInvokeListener() +
                        " please config jboot.shiro.invokeListener correct. ");
            }
        }

        return invokeListener;
    }

}
