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
package io.jboot.test;

import io.jboot.test.web.MockHttpServletRequest;
import io.jboot.test.web.MockHttpServletResponse;
import io.jboot.test.web.MockServletInputStream;
import io.jboot.utils.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MockMvc {

    private Consumer<MockHttpServletRequest> requestStartListener;
    private Consumer<MockHttpServletResponse> requestFinishedListener;

    public Consumer<MockHttpServletRequest> getRequestStartListener() {
        return requestStartListener;
    }

    public void setRequestStartListener(Consumer<MockHttpServletRequest> requestStartListener) {
        this.requestStartListener = requestStartListener;
    }

    public Consumer<MockHttpServletResponse> getRequestFinishedListener() {
        return requestFinishedListener;
    }

    public void setRequestFinishedListener(Consumer<MockHttpServletResponse> requestFinishedListener) {
        this.requestFinishedListener = requestFinishedListener;
    }

    public MockMvcResult get(String target) {
        return get(target, null);
    }


    public MockMvcResult get(String target, Map<String, Object> paras) {
        return get(target, paras, null);
    }


    public MockMvcResult get(String target, Map<String, Object> p, Map<String, String> headers) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        final Map<String, Object> paras = p != null ? p : new HashMap<>();


        int indexOf = target.lastIndexOf("?");
        if (indexOf != -1) {
            Map<String, String> targetParas = StrUtil.queryStringToMap(target.substring(indexOf + 1));
            paras.putAll(targetParas);
            target = target.substring(0, indexOf);
        }

        request.setServletPath(target);

        if (headers != null) {
            request.setHeaders(headers);
        }

        paras.forEach(request::addParameter);


        if (requestStartListener != null) {
            requestStartListener.accept(request);
        }

        return doStartMockRequest(request);
    }

    public MockMvcResult post(String target) {
        return post(target, null, null, null);
    }

    public MockMvcResult post(String target, String postData) {
        return post(target, null, null, postData);
    }


    public MockMvcResult post(String target, Map<String, Object> paras) {
        return post(target, paras, null, null);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, String postData) {
        return post(target, paras, null, postData);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, Map<String, String> headers, String postData) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");

        int indexOf = target.lastIndexOf("?");
        if (indexOf != -1) {
            Map<String, String> targetParas = StrUtil.queryStringToMap(target.substring(indexOf + 1));
            if (paras == null) {
                paras = new HashMap<>();
            }
            paras.putAll(targetParas);
            target = target.substring(0, indexOf);
        }

        request.setServletPath(target);

        if (headers != null) {
            request.setHeaders(headers);
        }

        if (paras != null) {
            paras.forEach(request::addParameter);
        }

        if (StrUtil.isNotBlank(postData)) {
            request.setInputStream(new MockServletInputStream(postData));
        }

        if (requestStartListener != null) {
            requestStartListener.accept(request);
        }

        return doStartMockRequest(request);
    }


    private MockMvcResult doStartMockRequest(MockHttpServletRequest request) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            sendRequest(request, response);
        } finally {
            if (requestFinishedListener != null) {
                requestFinishedListener.accept(response);
            }
        }

        return new MockMvcResult(response);
    }


    public void sendRequest(MockHttpServletRequest request, MockHttpServletResponse response) {
        MockApp.mockRequest(request, response);
    }

}
