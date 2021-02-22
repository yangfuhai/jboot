/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
public class JFinalEnumObject extends LinkedHashMap<String, Object> {


    private Map<String, Method> staticMethods;


    void init(Class<? extends Enum<?>> enumClass, Map<String, Method> staticMethods) {
        this.staticMethods = staticMethods;
        for (Enum<?> e : enumClass.getEnumConstants()) {
            put(e.name(), e);
        }
    }


    protected Object invokeEnumMethod(String methodName) {
        try {
            return staticMethods.get(methodName)
                    .invoke(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }

    protected Object invokeEnumMethod(String methodName, Object paras) {
        try {
            return staticMethods.get(methodName)
                    .invoke(null, paras);
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }


    protected Object invokeEnumMethod(String methodName, Object... paras) {
        try {
            return staticMethods.get(methodName)
                    .invoke(null, paras);
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }


//    public Object value(Object text){
//        return invokeEnumMethod("value",text);
//    }


    public static JFinalEnumObject create(Class<? extends Enum<?>> enumClass) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass objectCtClass = pool.getCtClass(Object.class.getName());
            CtClass supperClass = pool.get(JFinalEnumObject.class.getName());

            CtClass newClass = pool.makeClass(JFinalEnumObject.class.getName() + "." + enumClass.getSimpleName());
            newClass.setSuperclass(supperClass);
            newClass.setModifiers(Modifier.PUBLIC);

            Map<String, Method> enumStaticMethods = findEnumStaticMethods(enumClass);

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

            JFinalEnumObject ret = (JFinalEnumObject) newClass.toClass().newInstance();
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


    private static Map<String, Method> findEnumStaticMethods(Class<? extends Enum<?>> enumClass) {
        Map<String, Method> retMap = null;
        try {
            Method[] methods = enumClass.getDeclaredMethods();
            for (Method method : methods) {
                int methodModifiers = method.getModifiers();
                if (Modifier.isPublic(methodModifiers) && Modifier.isStatic(methodModifiers)) {
                    if (retMap == null) {
                        retMap = new HashMap<>();
                    }
                    retMap.put(method.getName(), method);
                }
            }
        } catch (Exception ex) {
            LogKit.logNothing(ex);
        }
        return retMap;
    }


}
