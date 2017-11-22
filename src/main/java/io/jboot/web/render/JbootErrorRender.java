/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import io.jboot.exception.JbootExceptionHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * JbootErrorRender.
 */
public class JbootErrorRender extends Render {

    protected static final String contentType = "text/html; charset=" + getEncoding();

    protected static final String poweredBy = "<center><a href='http://jboot.io' target='_blank'><b>Powered by Jboot</b></a></center>";

    protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1>404 Not Found</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1>401 Unauthorized</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1>403 Forbidden</h1></center><hr>" + poweredBy + "</body></html>";

    protected static final String html500 = "<html><head><title>500 Internal Server Error</title></head>" +
            "<body bgcolor='white'><center><h1>500 Internal Server Error</h1></center>" +
            "<hr>" +
            "%s" +
            "<hr>" + poweredBy +
            "</body></html>";

    protected int errorCode;

    public JbootErrorRender(int errorCode) {
        this.errorCode = errorCode;
    }

    public void render() {
        response.setStatus(getErrorCode());

        PrintWriter writer = null;
        try {
            response.setContentType(contentType);
            writer = response.getWriter();
            writer.write(getErrorHtml());
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public String getErrorHtml() {
        int errorCode = getErrorCode();
        if (errorCode == 404)
            return html404;
        if (errorCode == 401)
            return html401;
        if (errorCode == 403)
            return html403;
        if (errorCode == 500)
            return build500ErrorInfo();
        return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + poweredBy + "</body></html>";
    }

    public int getErrorCode() {
        return errorCode;
    }


    public String build500ErrorInfo() {
        List<Throwable> throwables = JbootExceptionHolder.throwables();
        if (throwables == null || throwables.size() == 0) {
            return String.format(html500, "");
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Throwable throwable : throwables) {
            stringBuilder.append(throwable.getClass().getName() + " : " + throwable.getMessage() + "<br />");
            StackTraceElement[] elems = throwable.getStackTrace();
            for (StackTraceElement element : elems) {
                stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at " + element + "<br />");
            }
        }

        return String.format(html500, stringBuilder.toString());
    }
}





