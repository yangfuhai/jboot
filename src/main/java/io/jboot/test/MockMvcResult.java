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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jboot.test.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import java.util.*;

public class MockMvcResult {

    final MockHttpServletResponse response;
    private JSONObject jsonObject;

    public MockMvcResult(MockHttpServletResponse response) {
        this.response = response;
    }

    public String getContent() {
        return response.getContentString();
    }

    public JSONObject getContentAsJSONObject() {
        if (jsonObject == null) {
            jsonObject = JSON.parseObject(getContent());
        }
        return jsonObject;
    }

    public String getContentType() {
        return response.getContentType();
    }

    public int getStatus() {
        return response.getStatus();
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null) {
            for (String headerName : headerNames) {
                headers.put(headerName, response.getHeader(headerName));
            }
        }
        return headers;
    }

    public Set<Cookie> getCookies() {
        return response.getCookies();
    }

    public String getCookie(String name) {
        for (Cookie cookie : response.getCookies()) {
            if (Objects.equals(name, cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


    public MockHttpServletResponse getResponse() {
        return response;
    }

    public MockMvcResult printResult() {
        System.out.println(this);
        return this;
    }

    public MockMvcResult printContent() {
        System.out.println(getContent());
        return this;
    }

    public MockMvcResult assertThat(MockMvcAsserter mockMvcAssert) {
        mockMvcAssert.doAssert(this);
        return this;
    }

    public MockMvcResult assertJson(MockMvcJsonAsserter mockMvcAssert) {
        mockMvcAssert.doAssert(this.getContentAsJSONObject());
        return this;
    }


    public MockMvcResult assertTrue(MockMvcTrueAsserter mockMvcTrueAsserter) {
        return assertTrue(mockMvcTrueAsserter, "MockMvc result can not match the asserter.");
    }


    public MockMvcResult assertTrue(MockMvcTrueAsserter mockMvcTrueAsserter, String message) {
        if (!mockMvcTrueAsserter.doAssert(this)) {
            throw new AssertionError(message);
        }
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Response:\n");
        builder.append("Headers >>> ").append(getHeaders()).append("\n");
        builder.append("Status  >>> ").append(getStatus()).append("\n");
        builder.append("Content >>> ").append(getContent()).append("\n");
        return builder.toString();
    }
}
