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
package io.jboot.web.render;

import com.jfinal.kit.JsonKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import io.jboot.utils.DateUtil;
import io.jboot.web.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/4/7
 */
public class JbootResponseEntityRender extends Render {

    private ResponseEntity responseEntity;

    public JbootResponseEntityRender(ResponseEntity responseEntity) {
        this.responseEntity = responseEntity;
    }

    @Override
    public void render() {

        PrintWriter writer = null;
        try {
            //默认输出 json，但是 responseEntity 可以配置 Header 覆盖这个输出
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.setStatus(responseEntity.getHttpStatus().value());

            Map<String, String> headers = responseEntity.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    response.setHeader(entry.getKey(), entry.getValue());
                }
            }

            Object body = responseEntity.getBody();
            String bodyString = null;
            if (body == null) {
                bodyString = "";
            } else if (body instanceof String) {
                bodyString = (String) body;
            } else if (body instanceof Date) {
                bodyString = DateUtil.toDateTimeString((Date) body);
            } else {
                JsonKit.toJson(body);
            }
            writer = response.getWriter();
            writer.write(bodyString);
            // writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        }

    }
}
