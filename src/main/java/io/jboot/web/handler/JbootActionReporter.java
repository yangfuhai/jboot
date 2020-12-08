/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.handler;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.ActionReporter;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.JsonKit;
import io.jboot.JbootConsts;
import io.jboot.support.jwt.JwtInterceptor;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.JbootController;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


/**
 * JbootActionReporter 参考 ActionReporter
 */
public class JbootActionReporter {

    private static final String title = "\nJboot-" + JbootConsts.VERSION + " action report -------- ";
    private static final String interceptMethodDesc = "(Lcom/jfinal/aop/Invocation;)V";
    private static int maxOutputLengthOfParaValue = 512;
    private static Writer writer = new SystemOutWriter();
    private static ActionReporter actionReporter = JFinal.me().getConstants().getActionReporter();

    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));


    public static void setMaxOutputLengthOfParaValue(int maxOutputLengthOfParaValue) {
        if (maxOutputLengthOfParaValue < 16) {
            throw new IllegalArgumentException("maxOutputLengthOfParaValue must more than 16");
        }
        JbootActionReporter.maxOutputLengthOfParaValue = maxOutputLengthOfParaValue;
    }

    public static void setWriter(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer can not be null");
        }
        JbootActionReporter.writer = writer;
    }


    /**
     * Report the action
     */
    public static final void report(String target, Controller controller, Action action, Invocation invocation, long time) {
        try {
            doReport(target, controller, action, invocation, time);
        } catch (Exception ex) {
            // 出错的情况，一般情况下是：用户自定义了自己的 classloader, 此 classloader 加载的 class 没有被添加到 javassist 的 ClassPool
            // 如何添加可以参考 jpress 的 插件加载
            actionReporter.report(target, controller, action);
        }
    }


    private static final void doReport(String target, Controller controller, Action action, Invocation invocation, long time) throws Exception {
        CtClass ctClass = ClassPool.getDefault().get(action.getControllerClass().getName());
        String desc = JbootActionReporterUtil.getMethodDescWithoutName(action.getMethod());
        CtMethod ctMethod = ctClass.getMethod(action.getMethodName(), desc);
        int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);

        StringBuilder sb = new StringBuilder(title).append(sdf.get().format(new Date(time))).append(" -------------------------\n");
        sb.append("Request     : ").append(controller.getRequest().getMethod()).append(" ").append(target).append("\n");
        Class cc = action.getMethod().getDeclaringClass();
        sb.append("Controller  : ").append(cc.getName()).append(".(").append(getClassFileName(cc)).append(".java:" + lineNumber + ")");
        if (JbootActionInvocation.isControllerInvoked()) {
            sb.append(ConsoleColor.GREEN_BRIGHT + " ---> invoked √" + ConsoleColor.RESET);
        } else {
            sb.append(ConsoleColor.RED_BRIGHT + " ---> skipped ×" + ConsoleColor.RESET);
        }
        sb.append("\nMethod      : ").append(JbootActionReporterUtil.getMethodString(action.getMethod())).append("\n");


        String urlParas = controller.getPara();
        if (urlParas != null) {
            sb.append("UrlPara     : ").append(urlParas).append("\n");
        }

        Interceptor[] inters = invocation instanceof JbootActionInvocation ? ((JbootActionInvocation) invocation).getInters() : action.getInterceptors();
        List<Interceptor> invokedInterceptors = JbootActionInvocation.getInvokedInterceptor();

        boolean printJwt = false;

        if (inters.length > 0) {
            sb.append("Interceptor : ");
            for (int i = 0; i < inters.length; i++) {
                if (i > 0) {
                    sb.append("\n              ");
                }
                Interceptor inter = inters[i];
                Class ic = inter.getClass();

                if (ic == JwtInterceptor.class) {
                    printJwt = true;
                }

                CtClass icClass = ClassPool.getDefault().get(ic.getName());
                CtMethod icMethod = icClass.getMethod("intercept", interceptMethodDesc);
                int icLineNumber = icMethod.getMethodInfo().getLineNumber(0);
                sb.append(icMethod.getDeclaringClass().getName()).append(".(").append(getClassFileName(ic)).append(".java:" + icLineNumber + ")");

                if (invokedInterceptors.contains(inter)) {
                    sb.append(ConsoleColor.GREEN_BRIGHT + " ---> invoked √" + ConsoleColor.RESET);
                } else {
                    sb.append(ConsoleColor.RED_BRIGHT + " ---> skipped ×" + ConsoleColor.RESET);
                }
            }
            sb.append("\n");
        }

        // print all parameters
        HttpServletRequest request = controller.getRequest();
        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            sb.append("Parameter   : ");
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    sb.append(name).append("=");
                    if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
                        sb.append(values[0].substring(0, maxOutputLengthOfParaValue)).append("...");
                    } else {
                        sb.append(values[0]);
                    }
                } else {
                    sb.append(name).append("[]={");
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(values[i]);
                    }
                    sb.append("}");
                }
                sb.append("  ");
            }
            sb.append("\n");
        }


        if (!"GET".equalsIgnoreCase(controller.getRequest().getMethod()) && !RequestUtil.isMultipartRequest(controller.getRequest())){
            sb.append("RawData     : ").append(controller.getRawData());
            sb.append("\n");
        }

        if (printJwt && controller instanceof JbootController) {
            sb.append("Jwt         : ").append(JsonKit.toJson(((JbootController) controller).getJwtParas()));
            sb.append("\n");
        }


        sb.append("----------------------------------- taked " + (System.currentTimeMillis() - time) + " ms --------------------------------\n\n\n");

        try {
            writer.write(sb.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            JbootActionInvocation.clear();
        }
    }


    private static String getClassFileName(Class clazz) {
        String classFileName = clazz.getName();
        if (classFileName.contains("$")) {
            int indexOf = classFileName.contains(".") ? classFileName.lastIndexOf(".") + 1 : 0;
            return classFileName.substring(indexOf, classFileName.indexOf("$"));
        } else {
            return clazz.getSimpleName();
        }
    }


    private static class SystemOutWriter extends Writer {
        @Override
        public void write(String str) throws IOException {
            System.out.print(str);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}


