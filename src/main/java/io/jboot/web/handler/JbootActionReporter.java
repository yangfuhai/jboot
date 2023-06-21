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
package io.jboot.web.handler;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.ActionReporter;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.JsonKit;
import com.jfinal.render.*;
import io.jboot.Jboot;
import io.jboot.JbootConsts;
import io.jboot.support.jwt.JwtInterceptor;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.ReflectUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.render.JbootReturnValueRender;
import javassist.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
    private static boolean reportEnable = Jboot.isDevMode();
    private static boolean colorRenderEnable = true;
    private static boolean reportAllText = false;

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

    public static Writer getWriter() {
        return writer;
    }

    public static boolean isReportEnable() {
        return reportEnable;
    }

    public static void setReportEnable(boolean reportEnable) {
        JbootActionReporter.reportEnable = reportEnable;
    }

    public static boolean isReportAllText() {
        return reportAllText;
    }

    public static void setReportAllText(boolean reportAllText) {
        JbootActionReporter.reportAllText = reportAllText;
    }

    public static boolean isColorRenderEnable() {
        return colorRenderEnable;
    }

    public static void setColorRenderEnable(boolean colorRenderEnable) {
        JbootActionReporter.colorRenderEnable = colorRenderEnable;
    }

    /**
     * Report the action
     */
    public static void report(String target, Controller controller, Action action, Invocation invocation, long time) {
        try {
            doReport(target, controller, action, invocation, time);
        }
        // 在 tomcat 或者自定义 classloader 的情况下，
        // 可能会出现 NotFoundException 错误
        catch (NotFoundException e) {
            ClassPool.getDefault().insertClassPath(new ClassClassPath(controller.getClass()));
            try {
                doReport(target, controller, action, invocation, time);
            } catch (Exception exception) {
                actionReporter.report(target, controller, action);
            }
        } catch (Exception ex) {
            actionReporter.report(target, controller, action);
        } finally {
            JbootActionReporterInvocation.clear();
        }
    }


    private static void doReport(String target, Controller controller, Action action, Invocation invocation, long time) throws Exception {
        CtClass ctClass = ClassPool.getDefault().get(ClassUtil.getUsefulClass(action.getControllerClass()).getName());
        ClassPool.getDefault().get(ClassUtil.getUsefulClass(action.getControllerClass()).getName());
        String desc = JbootActionReporterUtil.getMethodDescWithoutName(action.getMethod());
        CtMethod ctMethod = ctClass.getMethod(action.getMethodName(), desc);
        int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);

        StringBuilder sb = new StringBuilder(title).append(sdf.get().format(new Date(time))).append(" -------------------------\n");
        sb.append("Request     : ").append(controller.getRequest().getMethod()).append(" ").append(target).append("\n");
        Class<?> cc = action.getMethod().getDeclaringClass();
        sb.append("Controller  : ").append(cc.getName()).append(".(").append(getClassFileName(cc)).append(".java:" + lineNumber + ")");
        if (JbootActionReporterInvocation.isControllerInvoked()) {
            sb.append((colorRenderEnable ? ConsoleColor.GREEN_BRIGHT : "") + " ---> invoked √" + (colorRenderEnable ? ConsoleColor.RESET : ""));
        } else {
            sb.append((colorRenderEnable ? ConsoleColor.RED_BRIGHT : "") + " ---> skipped ×" + (colorRenderEnable ? ConsoleColor.RESET : ""));
        }
        sb.append("\nMethod      : ").append(JbootActionReporterUtil.getMethodString(action.getMethod())).append("\n");


        String urlParas = controller.getPara();
        if (urlParas != null) {
            sb.append("UrlPara     : ").append(urlParas).append("\n");
        }

        Interceptor[] inters = invocation instanceof JbootActionReporterInvocation ? ((JbootActionReporterInvocation) invocation).getInters() : action.getInterceptors();
        List<Interceptor> invokedInterceptors = JbootActionReporterInvocation.getInvokedInterceptor();

        boolean printJwt = false;

        if (inters.length > 0) {
            sb.append("Interceptor : ");
            for (int i = 0; i < inters.length; i++) {
                if (i > 0) {
                    sb.append("\n              ");
                }
                Interceptor inter = inters[i];
                Class<?> interClass = ClassUtil.getUsefulClass(inter.getClass());

                if (interClass == JwtInterceptor.class) {
                    printJwt = true;
                }

                CtClass icClass = ClassPool.getDefault().get(interClass.getName());
                CtMethod icMethod = icClass.getMethod("intercept", interceptMethodDesc);
                int icLineNumber = icMethod.getMethodInfo().getLineNumber(0);
                sb.append(icMethod.getDeclaringClass().getName()).append(".(").append(getClassFileName(interClass)).append(".java:" + icLineNumber + ")");

                if (invokedInterceptors.contains(inter)) {
                    sb.append((colorRenderEnable ? ConsoleColor.GREEN_BRIGHT : "") + " ---> invoked √" + (colorRenderEnable ? ConsoleColor.RESET : ""));
                } else {
                    sb.append((colorRenderEnable ? ConsoleColor.RED_BRIGHT : "") + " ---> skipped ×" + (colorRenderEnable ? ConsoleColor.RESET : ""));
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
                        sb.append(values[0], 0, maxOutputLengthOfParaValue).append("...");
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


        if (!"GET".equalsIgnoreCase(controller.getRequest().getMethod())
                && !RequestUtil.isMultipartRequest(controller.getRequest())
                && StrUtil.isNotBlank(controller.getRawData())) {
            sb.append("RawData     : ").append(controller.getRawData());
            sb.append("\n");
        }

        if (printJwt && controller instanceof JbootController) {
            String jwtString = JsonKit.toJson(((JbootController) controller).getJwtParas());
            if (StrUtil.isNotBlank(jwtString)) {
                sb.append("Jwt         : ").append(jwtString.replace("\n", ""));
                sb.append("\n");
            }
        }

        appendRenderMessage(controller.getRender(), sb);

        sb.append("----------------------------------- took " + (System.currentTimeMillis() - time) + " ms --------------------------------\n\n\n");

        writer.write(sb.toString());
    }

    private static void appendRenderMessage(Render render, StringBuilder sb) {
        if (render == null) {
            return;
        }
        String view = render.getView();
        if (StrUtil.isNotBlank(view)) {
            sb.append("Render      : ").append(view);
        } else if (render instanceof JsonRender) {
            String jsontext = ((JsonRender) render).getJsonText();
            if (jsontext == null) {
                jsontext = "";
            }
            jsontext = jsontext.replace("\n", "");
            sb.append("Render      : ").append(getRenderText(jsontext));
        } else if (render instanceof TextRender) {
            String text = ((TextRender) render).getText();
            if (text == null) {
                text = "";
            }
            text = text.replace("\n", "");
            sb.append("Render      : ").append(getRenderText(text));
        } else if (render instanceof FileRender) {
            File file = ReflectUtil.getFieldValue(render, "file");
            sb.append("Render      : ").append(file);
        } else if (render instanceof RedirectRender) {
            String url = ReflectUtil.getFieldValue(render, "url");
            sb.append("Redirect    : ").append(url);
        } else if (render instanceof NullRender) {
            sb.append("Render      :  null");
        } else if (render instanceof JbootReturnValueRender) {
            appendRenderMessage(((JbootReturnValueRender) render).getRealRender(), sb);
        } else {
            sb.append("Render      : ").append(ClassUtil.getUsefulClass(render.getClass()).getName());
        }
        sb.append("\n");
    }


    private static String getRenderText(String orignalText) {
        if (StrUtil.isBlank(orignalText)) {
            return "";
        }

        if (!reportAllText && orignalText.length() > 100) {
            return orignalText.substring(0, 100) + "...";
        }

        return orignalText;
    }


    private static String getClassFileName(Class<?> clazz) {
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


