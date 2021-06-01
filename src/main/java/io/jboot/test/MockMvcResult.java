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

import io.jboot.test.web.MockHttpServletResponse;

public class MockMvcResult {

    final MockHttpServletResponse response;

    public MockMvcResult(MockHttpServletResponse response) {
        this.response = response;
    }

    public String getContent() {
        return response.getContentString();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public int getHttpCode() {
        return response.getStatus();
    }

    public MockHttpServletResponse getResponse() {
        return response;
    }

    public MockMvcResult printResult() {
        System.out.println(this);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Content-Type:").append("\n").append(getContentType()).append("\n\n");
        sb.append("Http-Code:").append("\n").append(getHttpCode()).append("\n\n");
        sb.append("Content:").append("\n").append(getContent()).append("\n\n");
        return sb.toString();
    }
}
