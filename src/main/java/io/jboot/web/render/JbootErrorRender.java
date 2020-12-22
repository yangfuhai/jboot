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
package io.jboot.web.render;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.utils.RequestUtil;

import java.io.PrintWriter;
import java.util.List;

/**
 * JbootErrorRender.
 */
public class JbootErrorRender extends Render {

    protected static final String htmlContentType = "text/html;charset=" + getEncoding();
    protected static final String jsonContentType = "application/json;charset=" + getEncoding();

    protected static final String poweredBy = "<center><a href='http://jboot.io' target='_blank'><b>Powered by Jboot</b></a></center>";

    protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1>404 Not Found</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1>401 Unauthorized</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1>403 Forbidden</h1></center><hr>" + poweredBy + "</body></html>";

    protected static final String html500_header = "<html><head><title>500 Internal Server Error</title></head>" +
            "<body bgcolor='white'><center><h1>500 Internal Server Error</h1></center>" +
            "<hr>";

    protected static final String html500_footer = "<hr>" + poweredBy + "</body></html>";


    protected static final String json401 = JsonKit.toJson(Ret.fail().set("errorCode", 401).set("message", "401 Unauthorized"));
    protected static final String json403 = JsonKit.toJson(Ret.fail().set("errorCode", 403).set("message", "403 Forbidden"));
    protected static final String json404 = JsonKit.toJson(Ret.fail().set("errorCode", 404).set("message", "404 Not Found"));


    protected int errorCode;

    public JbootErrorRender(int errorCode, String view) {
        this.errorCode = errorCode;
        this.view = view;
    }

    @Override
    public void render() {
        response.setStatus(getErrorCode());


        //render with view
        String view = getView();
        if (view != null) {
            RenderManager.me().getRenderFactory()
                    .getRender(view)
                    .setContext(request, response)
                    .render();
            return;
        }

        try {
            boolean isJconContentType = RequestUtil.isJsonContentType(request);
            response.setContentType(isJconContentType ? jsonContentType : htmlContentType);
            PrintWriter writer = response.getWriter();
            writer.write(isJconContentType ? getErrorJson() : getErrorHtml());
        } catch (Exception ex) {
            throw new RenderException(ex);
        }

    }


    public String getErrorHtml() {
        int errorCode = getErrorCode();
        if (errorCode == 404) {
            return html404;
        }
        if (errorCode == 401) {
            return html401;
        }
        if (errorCode == 403) {
            return html403;
        }
        if (errorCode == 500) {
            return build500ErrorInfo();
        }
        return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + poweredBy + "</body></html>";
    }


    public String getErrorJson() {
        int errorCode = getErrorCode();
        if (errorCode == 404) {
            return json404;
        }
        if (errorCode == 401) {
            return json401;
        }
        if (errorCode == 403) {
            return json403;
        }
        if (errorCode == 500 || errorCode == 400) {
            return buildErrorJson();
        }

        return JsonKit.toJson(Ret.fail().set("errorCode", errorCode).set("message", errorCode + " Error"));
    }


    public int getErrorCode() {
        return errorCode;
    }


    public String build500ErrorInfo() {
        StringBuilder stringBuilder = new StringBuilder(html500_header);

        List<String> messages = JbootExceptionHolder.getMessages();
        for (String message : messages) {
            stringBuilder.append(message).append("<br />");
        }

        List<Throwable> throwables = JbootExceptionHolder.getThrowables();
        for (Throwable throwable : throwables) {
            stringBuilder.append(throwable.getClass().getName() + " : " + throwable.getMessage()).append("<br />");
            StackTraceElement[] elems = throwable.getStackTrace();
            for (StackTraceElement element : elems) {
                stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at ")
                        .append(element)
                        .append("<br />");
            }
        }

        return stringBuilder.append(html500_footer).toString();
    }


    public String buildErrorJson() {

        Ret ret = Ret.fail().set("errorCode", getErrorCode()).set("message", getErrorCode() + " Internal Server Error");

        StringBuilder errorMsgBuilder = new StringBuilder();
        List<String> messages = JbootExceptionHolder.getMessages();
        for (String message : messages) {
            errorMsgBuilder.append(message);
        }

        StringBuilder throwableMsgBuilder = new StringBuilder();
        List<Throwable> throwables = JbootExceptionHolder.getThrowables();
        for (Throwable throwable : throwables) {
            throwableMsgBuilder.append(throwable.getClass().getName() + ": " + throwable.getMessage());
        }

        return JsonKit.toJson(ret.set("errorMessage", errorMsgBuilder.toString()).set("throwable", throwableMsgBuilder.toString()));
    }
}





