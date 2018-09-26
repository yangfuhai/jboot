/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.http;

import io.jboot.utils.StrUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class JbootHttpBase implements JbootHttp {

    public static String buildParams(JbootHttpRequest request) throws UnsupportedEncodingException {
        Map<String, Object> params = request.getParams();
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey() != null && StrUtils.isNotBlank(entry.getValue()))
                builder.append(entry.getKey().trim()).append("=")
                        .append(URLEncoder.encode(entry.getValue().toString(), request.getCharset())).append("&");
        }

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }


    public static void buildGetUrlWithParams(JbootHttpRequest request) throws UnsupportedEncodingException {

        String params = buildParams(request);

        if (StrUtils.isBlank(params)) {
            return;
        }

        String originUrl = request.getRequestUrl();
        if (originUrl.contains("?")) {
            originUrl = originUrl + "&" + params;
        } else {
            originUrl = originUrl + "?" + params;
        }
        request.setRequestUrl(originUrl);

    }


}
