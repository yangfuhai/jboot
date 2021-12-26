package io.jboot.app;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 参考 JFinal Undertow 的 PathKitExt，目的是在启动的时候 为 PathKit 配置参数
 */
public class PathKitExt {

    private static String locationPath = null;    // 定位路径

    private static String rootClassPath = null;
    private static String webRootPath = null;

    /**
     * 1：获取 PathKitExt 类文件所处 jar 包文件所在的目录，注意在 "非部署" 环境中获取到的
     * 通常是 maven 本地库中的某个目录，因为在开发时项目所依赖的 jar 包在 maven 本地库中
     * 这种情况不能使用
     * <p>
     * 2：PathKitExt 自身在开发时，也就是未打成 jar 包时，获取到的是 APP_BASE/target/classes
     * 这种情况多数不必关心，因为 PathKitExt 在使用时必定处于 jar 包之中
     * <p>
     * 3：获取到的 locationPath 目录用于生成部署时的 config 目录，该值只会在 "部署" 环境下被获取
     * 也用于生成 webRootPath、rootClassPath，这两个值也只会在 "部署" 时被获取
     * 这样就兼容了部署与非部署两种场景
     * <p>
     * 注意：该路径尾部的 "/" 或 "\\" 已被去除
     */
    public static String getLocationPath() {
        if (locationPath != null) {
            return locationPath;
        }

        try {
            // Class<?> clazz = io.undertow.Undertow.class;		// 仅测试用
            Class<?> clazz = PathKitExt.class;
            String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = java.net.URLDecoder.decode(path, "UTF-8");
            path = path.trim();
            File file = new File(path);
            if (file.isFile()) {
                path = file.getParent();
            }

            path = removeSlashEnd(path);        // 去除尾部 '/' 或 '\' 字符
            locationPath = path;

            return locationPath;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRootClassPath() {
        if (rootClassPath == null) {
            rootClassPath = buildRootClassPath();
        }
        return rootClassPath;
    }


    private static String buildRootClassPath() {
        String classPathDirEndsWith_classes = getClassPathDirEndsWith_classes();
        if (classPathDirEndsWith_classes != null) {
            return classPathDirEndsWith_classes;
        }

        String path = getLocationPath();
        return processRootClassPath(path);
    }

    /**
     * 获取以 "classes" 结尾的 class path
     */
    private static String getClassPathDirEndsWith_classes() {
        String[] classPathDirs = getClassPathDirs();
        if (classPathDirs == null || classPathDirs.length == 0) {
            return null;
        }

        for (String dir : classPathDirs) {
            if (dir != null) {
                dir = removeSlashEnd(dir.trim());
                if (dir != null && dir.endsWith("classes")) {
                    return dir;
                }
            }
        }

        return null;
    }

    /**
     * 1：开发环境 path 会以 classes 结尾
     * <p>
     * 2：打包以后的部署环境不会以 classes 结尾，约定一个合理的项目打包结构
     * 暂时约定 APP_BASE/config 为 rootClassPath，因为要读取外部配置文件
     */
    private static String processRootClassPath(String path) {
        if (path.endsWith("classes")) {
            return path;
        }

        if (path.endsWith(File.separatorChar + "lib")) {
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
        }

        return new File(path + File.separator + "config").getAbsolutePath();
    }

    public static String removeSlashEnd(String path) {
        if (path.endsWith(File.separator)) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

    // --------------------------------------------------------------------------------------

    public static String getWebRootPath() {
        if (webRootPath == null) {
            webRootPath = buildWebRootPath();
        }
        return webRootPath;
    }

    private static String buildWebRootPath() {
        String classPathDirEndsWith_classes = getClassPathDirEndsWith_classes();
        if (classPathDirEndsWith_classes != null) {
            return classPathDirEndsWith_classes;
        }

        String path = getLocationPath();
        return processWebRootPath(path);
    }

    private static String processWebRootPath(String path) {
        if (path.endsWith("classes")) {
            return path;
        }

        if (path.endsWith(File.separatorChar + "lib")) {
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
        }

        return new File(path + File.separator + "webapp").getAbsolutePath();
    }

    // ---------

    private static String[] classPathDirs = null;

    public static String[] getClassPathDirs() {
        if (classPathDirs == null) {
            classPathDirs = buildClassPathDirs();
        }
        return classPathDirs;
    }

    private static String[] buildClassPathDirs() {
        List<String> list = new ArrayList<>();
        String[] classPathArray = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String classPath : classPathArray) {
            classPath = classPath.trim();

            if (classPath.startsWith("./")) {
                classPath = classPath.substring(2);
            }

            File file = new File(classPath);
            if (file.exists() && file.isDirectory()) {
                // if (!classPath.endsWith("/") && !classPath.endsWith("\\")) {
                if (!classPath.endsWith(File.separator)) {
                    classPath = classPath + File.separator;        // append postfix char "/"
                }

                list.add(classPath);
            }
        }
        return list.toArray(new String[list.size()]);
    }

}
