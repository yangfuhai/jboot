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
package io.jboot.web.render;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.components.valid.ValidException;

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
    protected static final String html400_header = "<html><head><title>400 Internal Server Error</title></head>" +
            "<body bgcolor='white'><center><h1>400 Internal Server Error</h1></center>" +
            "<hr>";

    protected static final String html500_footer = "<hr>" + poweredBy + "</body></html>";


    protected static final String json401 = JsonKit.toJson(Ret.fail().set("errorCode", 401).set("message", "401 Unauthorized"));
    protected static final String json403 = JsonKit.toJson(Ret.fail().set("errorCode", 403).set("message", "403 Forbidden"));
    protected static final String json404 = JsonKit.toJson(Ret.fail().set("errorCode", 404).set("message", "404 Not Found"));


    protected int errorCode;
    protected String message;
    protected Throwable throwable;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

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
            boolean needRenderJson = RequestUtil.isJsonContentType(request) || RequestUtil.isAjaxRequest(request);
            response.setContentType(needRenderJson ? jsonContentType : htmlContentType);
            PrintWriter writer = response.getWriter();
            writer.write(needRenderJson ? getErrorJson() : getErrorHtml());
        } catch (Exception ex) {
            throw new RenderException(ex);
        }

    }


    public String getErrorHtml() {
        int errorCode = getErrorCode();
        if (throwable instanceof ValidException) {
            return buildErrorInfo(html400_header);
        } else if (errorCode == 404) {
            return html404;
        } else if (errorCode == 401) {
            return html401;
        } else if (errorCode == 403) {
            return html403;
        } else if (errorCode == 400) {
            return buildErrorInfo(html400_header);
        } else if (errorCode == 500) {
            return buildErrorInfo(html500_header);
        }
        return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + poweredBy + "</body></html>";
    }


    public String buildErrorInfo(String headerHtml) {
        StringBuilder stringBuilder = new StringBuilder(headerHtml);

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


    public String getErrorJson() {
        int errorCode = getErrorCode();
        if (throwable instanceof ValidException || errorCode == 500 || errorCode == 400) {
            return buildErrorJson();
        } else if (errorCode == 404) {
            return json404;
        } else if (errorCode == 401) {
            return json401;
        } else if (errorCode == 403) {
            return json403;
        }
        return JsonKit.toJson(Ret.fail().set("errorCode", errorCode).set("message", errorCode + " Error"));
    }


    public String buildErrorJson() {

        Ret ret = Ret.fail().set("errorCode", getErrorCode()).set("message", getErrorCode() + " Internal Server Error");

        StringBuilder errorMsgBuilder = new StringBuilder();
        List<String> messages = JbootExceptionHolder.getMessages();
        for (String message : messages) {
            errorMsgBuilder.append(message);
        }

        ret.set("errorMessage", errorMsgBuilder.toString());

        List<Throwable> throwables = JbootExceptionHolder.getThrowables();
        if (throwables.size() > 0) {
            Throwable throwable = throwables.get(0);
            ret.set("throwable", throwable.getClass().getName() + ": " + throwable.getMessage());
            ret.set("message", throwable.getMessage());
        }

        if (this.throwable != null) {
            ret.set("throwable", this.throwable.getClass().getName() + ": " + this.throwable.getMessage());
            ret.set("message", throwable.getMessage());

            if (throwable instanceof ValidException) {
                ret.set("errorMessage", ((ValidException) throwable).getReason());
            }
        }

        if (StrUtil.isNotBlank(this.message)) {
            ret.set("message", this.message);
        }

        return JsonKit.toJson(ret);
    }
}





