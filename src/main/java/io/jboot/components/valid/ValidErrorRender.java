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
package io.jboot.components.valid;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import io.jboot.utils.RequestUtil;

/**
 * 数据验证错误的渲染器，可以通过实现 JbootRenderFactory: {@link io.jboot.web.render.JbootRenderFactory} 来实现自定义渲染验证错误
 */
public class ValidErrorRender extends Render {

    protected static final String htmlContentType = "text/html;charset=" + getEncoding();
    protected static final String jsonContentType = "application/json;charset=" + getEncoding();
    protected static final String html_header = "<html><head><title>Invalid parameter</title></head>" +
            "<body bgcolor='white'><center><h1>Invalid parameter</h1></center>" +
            "<hr>";

    protected static final String poweredBy = "<center><a href='http://jboot.io' target='_blank'><b>Powered by Jboot</b></a></center>";
    protected static final String html_footer = "<hr>" + poweredBy + "</body></html>";

    protected int errorCode = ValidUtil.getErrorCode();
    protected final ValidException validException;

    public ValidErrorRender(ValidException validException) {
        this.validException = validException;
    }

    @Override
    public void render() {
        try {
            if (RequestUtil.isJsonContentType(request) || RequestUtil.isAjaxRequest(request)) {
                response.setStatus(200);
                response.setContentType(jsonContentType);
                response.getWriter().write(getErrorJson());
            } else {
                response.setStatus(errorCode);
                response.setContentType(htmlContentType);
                response.getWriter().write(getErrorHtml());
            }
        } catch (Exception ex) {
            throw new RenderException(ex);
        }
    }

    public String getErrorHtml() {
        StringBuilder html = new StringBuilder(html_header);
        html.append(validException.getFormName() == null ? "" : (validException.getFormName() + ": "));
        html.append(validException.getMessage()).append("<br />");
        return html.append(html_footer).toString();
    }

    public String getErrorJson() {
        Ret ret = Ret.fail(validException.getMessage()).set("errorCode", errorCode);
        ret.set("throwable", validException.getClass().getName() + ": " + this.validException.getMessage());
        ret.set("errorMessage", validException.getReason());
        ret.set("formName", validException.getFormName());
        return JsonKit.toJson(ret);
    }
}
