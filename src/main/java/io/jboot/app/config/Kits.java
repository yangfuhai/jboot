package io.jboot.app.config;


import com.jfinal.kit.PathKit;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class Kits {


    public static boolean isBlank(String string) {
        return string == null || string.trim().equals("");
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Method> getClassSetMethods(Class clazz) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")
                    && method.getName().length() > 3
                    && method.getParameterCount() == 1) {

                setMethods.add(method);
            }
        }
        return setMethods;
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }


    private static String rootClassPath;

    public static String getRootClassPath() {
        if (rootClassPath == null) {
            try {
                String path = getClassLoader().getResource("").toURI().getPath();
                rootClassPath = new File(path).getAbsolutePath();
            } catch (Exception e) {
                try {
                    String path = PathKit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                    if (path.endsWith(File.separator)) {
                        path = path.substring(0, path.length() - 1);
                    }
                    rootClassPath = path;
                } catch (UnsupportedEncodingException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        return rootClassPath;
    }


    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : PathKit.class.getClassLoader();
    }

    public static void doNothing(Throwable ex) {
    }
}
