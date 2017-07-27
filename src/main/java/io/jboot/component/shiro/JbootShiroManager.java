/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.shiro;

import com.jfinal.config.Routes;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import io.jboot.component.shiro.processer.*;
import io.jboot.utils.ArrayUtils;
import org.apache.shiro.authz.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * shiro 管理器.
 */
public class JbootShiroManager {
    private static JbootShiroManager me = new JbootShiroManager();

    private JbootShiroManager() {
    }


    public static JbootShiroManager me() {
        return me;
    }

    private static final String SLASH = "/";
    private ConcurrentHashMap<String, ShiroAuthorizeProcesserInvoker> invokers = new ConcurrentHashMap<>();

    private ShiroRequiresAuthenticationProcesser requiresAuthenticationProcesser = new ShiroRequiresAuthenticationProcesser();
    private ShiroRequiresUserProcesser requiresUserProcesser = new ShiroRequiresUserProcesser();
    private ShiroRequiresGuestProcesser requiresGuestProcesser = new ShiroRequiresGuestProcesser();

    private Routes routes;


    public void init(Routes routes) {
        this.routes = routes;
        initInvokers();
    }

    /**
     * 初始化 invokers 变量
     */
    private void initInvokers() {
        Set<String> excludedMethodName = buildExcludedMethodName();

        for (Routes.Route route : routes.getRouteItemList()) {
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


    public AuthorizeResult invoke(String actionKey) {
        ShiroAuthorizeProcesserInvoker invoker = invokers.get(actionKey);
        if (invoker == null) {
            return AuthorizeResult.ok();
        }

        return invoker.invoke();
    }


}
