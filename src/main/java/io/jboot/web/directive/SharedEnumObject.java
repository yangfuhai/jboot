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
package io.jboot.web.directive;

import com.jfinal.kit.LogKit;
import com.jfinal.template.expr.ast.MethodKeyBuilder;
import io.jboot.app.JdkUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JFinalEnumObject 的主要目的是为了动态创建一个包装 Enum 枚举的类，方便模板引擎直接调用。
 * <p>
 * 添加枚举类型，便于在模板中使用
 *
 * <pre>
 * 例子：
 * 1：定义枚举类型
 *
 * @JFinalSharedEnum
 * public enum UserType {
 *
 *   ADMIN(1,"管理员"),
 *   USER(2,"用户");
 *
 *   private int value;
 *   private String text;
 *
 *   UserType(int value, String text) {
 *     this.value = value;
 *     this.text = text;
 *   }
 *
 *
 *    public static String text(Integer value) {
 *          for (UserType type : values()) {
 *                 if (type.value == value) {
 *                     return type.text;
 *                 }
 *           }
 *         return null
 *    }
 *
 * }
 *
 *
 * 2：模板中使用
 * ### 以下的对象 u 通过 Controller 中的 setAttr("u", UserType.ADMIN) 传递
 *
 * #if( u == UserType.ADMIN )
 *    #(UserType.ADMIN)
 *
 *    #(UserType.ADMIN.name())
 *
 *    #(UserType.ADMIN.hello())
 * #end
 *
 * 或者
 *
 *  #(UserType.text(1))
 *
 * </pre>
 */
public class SharedEnumObject extends LinkedHashMap<String, Object> {


    private Class<? extends Enum<?>> enumClass;
    private Map<Long, Method> staticMethods;


    void init(Class<? extends Enum<?>> enumClass, Map<Long, Method> staticMethods) {
        this.enumClass = enumClass;
        this.staticMethods = staticMethods;
        for (Enum<?> e : enumClass.getEnumConstants()) {
            put(e.name(), e);
        }
    }


    protected Object invokeEnumMethod(String methodName) {
        return doInvokeEnumMethod(methodName);
    }

    protected Object invokeEnumMethod(String methodName, Object para1) {
        return doInvokeEnumMethod(methodName, para1);
    }


    protected Object invokeEnumMethod(String methodName, Object para1, Object para2) {
        return doInvokeEnumMethod(methodName, para1, para2);
    }

    protected Object invokeEnumMethod(String methodName, Object para1, Object para2, Object para3) {
        return doInvokeEnumMethod(methodName, para1, para2, para3);
    }

    protected Object invokeEnumMethod(String methodName, Object para1, Object para2, Object para3, Object para4) {
        return doInvokeEnumMethod(methodName, para1, para2, para3, para4);
    }

    protected Object invokeEnumMethod(String methodName, Object para1, Object para2, Object para3, Object para4, Object para5) {
        return doInvokeEnumMethod(methodName, para1, para2, para3, para4, para5);
    }

    private Object doInvokeEnumMethod(String methodName, Object... paras) {
        Method method = findMethod(methodName, paras);
        if (method == null) {
            throw new RuntimeException("Can not find the method: " + methodName + " with paras: " + Arrays.toString(paras) + " in enum: " + this.enumClass);
        }
        try {
            return method.invoke(null, paras);
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }


//    public Object value(Object text){
//        return invokeEnumMethod("value",text);
//    }

    public Object invoke(String method, Object... paras) {
        return doInvokeEnumMethod(method, paras);
    }


    public static SharedEnumObject create(Class<? extends Enum<?>> enumClass) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass objectCtClass = pool.getCtClass(Object.class.getName());
            CtClass supperClass = pool.get(SharedEnumObject.class.getName());

            CtClass newClass = pool.makeClass(SharedEnumObject.class.getName() + "_$$_" + enumClass.getSimpleName());
            newClass.setSuperclass(supperClass);
            newClass.setModifiers(Modifier.PUBLIC);

            Map<Long, Method> enumStaticMethods = findEnumStaticMethods(enumClass);

            if (enumStaticMethods != null) {
                for (Method originalMethod : enumStaticMethods.values()) {
                    boolean isReturnVoid = (void.class == originalMethod.getReturnType());
                    CtClass returnClass = isReturnVoid ? CtClass.voidType : objectCtClass;

                    CtClass[] parameterClassArray = createParameterClassArray(originalMethod, pool);
                    CtMethod ctMethod = new CtMethod(returnClass, originalMethod.getName(), parameterClassArray, newClass);
                    ctMethod.setModifiers(Modifier.PUBLIC);

                    if (isReturnVoid) {
                        ctMethod.setBody("{invokeEnumMethod(\"" + originalMethod.getName() + "\",$$);}");
                    } else {
                        ctMethod.setBody("{return invokeEnumMethod(\"" + originalMethod.getName() + "\",$$);}");
                    }

                    newClass.addMethod(ctMethod);
                }
            }

            // jdk 17
            // toClass() must add neighbor class in jdk17
            // neighbor: A class belonging to the same package that this class belongs to

            SharedEnumObject ret = JdkUtil.isJdk1x() ? (SharedEnumObject) newClass.toClass().newInstance()
                    : (SharedEnumObject) newClass.toClass(SharedEnumObject.class).newInstance();
            ret.init(enumClass, enumStaticMethods);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static CtClass[] createParameterClassArray(Method originalMethod, ClassPool pool) throws NotFoundException {
        if (originalMethod.getParameterCount() == 0) {
            return new CtClass[0];
        }
        CtClass[] ret = new CtClass[originalMethod.getParameterCount()];
        int index = 0;
        for (Class<?> clazz : originalMethod.getParameterTypes()) {
            ret[index++] = pool.getCtClass(clazz.getName());
        }
        return ret;
    }


    private static Map<Long, Method> findEnumStaticMethods(Class<? extends Enum<?>> enumClass) {
        Map<Long, Method> retMap = null;
        try {
            Method[] methods = enumClass.getDeclaredMethods();
            for (Method method : methods) {
                int methodModifiers = method.getModifiers();
                if (Modifier.isPublic(methodModifiers) && Modifier.isStatic(methodModifiers)) {
                    if (retMap == null) {
                        retMap = new HashMap<>();
                    }
                    retMap.put(getMethodKey(enumClass, method.getName(), method.getParameterTypes()), method);
                }
            }
        } catch (Exception ex) {
            LogKit.logNothing(ex);
        }
        return retMap;
    }


    private Method findMethod(String methodName, Object... args) {
        Class<?>[] argTypes = null;
        if (args != null && args.length > 0) {
            argTypes = new Class[args.length];
            int index = 0;
            for (Object arg : args) {
                argTypes[index++] = arg != null ? arg.getClass() : null;
            }
        }

        long key = getMethodKey(this.enumClass, methodName, argTypes);
        Method method = this.staticMethods.get(key);
        if (method != null) {
            return method;
        }

        for (Method m : this.staticMethods.values()) {
            if (m.getName().equals(methodName) && isMatchParas(m.getParameterTypes(), args)) {
                this.staticMethods.put(key, m);
                return m;
            }
        }

        return null;
    }

    private boolean isMatchParas(Class<?>[] parameterTypes, Object[] args) {
        if (args == null || args.length == 0) {
            return parameterTypes.length == 0;
        }

        if (parameterTypes.length != args.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (args[i] != null && !parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                return false;
            }
        }
        return true;
    }


    private static Long getMethodKey(Class<?> targetClass, String methodName, Class<?>[] argTypes) {
        return MethodKeyBuilder.getInstance().getMethodKey(targetClass, methodName, argTypes);
    }


}
