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
package io.jboot.test;

import io.jboot.components.http.HttpMimeTypes;
import io.jboot.test.web.MockHttpServletRequest;
import io.jboot.test.web.MockHttpServletResponse;
import io.jboot.test.web.MockServletInputStream;
import io.jboot.utils.StrUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class MockMvc {

    protected boolean holdCookiesEnable = false;
    protected Set<Cookie> holdCookies = new HashSet<>();

    protected Consumer<MockHttpServletRequest> requestStartListener;
    protected Consumer<MockHttpServletResponse> requestFinishedListener;

    public MockMvc() {
    }

    public MockMvc(boolean holdCookiesEnable) {
        this.holdCookiesEnable = holdCookiesEnable;
    }

    public boolean isHoldCookiesEnable() {
        return holdCookiesEnable;
    }

    public void setHoldCookiesEnable(boolean holdCookiesEnable) {
        this.holdCookiesEnable = holdCookiesEnable;
    }

    public Set<Cookie> getHoldCookies() {
        return holdCookies;
    }

    public String getCookieValue(String name) {
        for (Cookie holdCookie : holdCookies) {
            if (holdCookie.getName().equals(name)) {
                return holdCookie.getValue();
            }
        }
        return null;
    }

    public void setHoldCookies(Set<Cookie> holdCookies) {
        this.holdCookies = holdCookies;
    }

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
        return get(target, null, null, null);
    }


    public MockMvcResult get(String target, Map<String, Object> paras) {
        return get(target, paras, null, null);
    }


    public MockMvcResult get(String target, Map<String, Object> p, Set<Cookie> cookies) {
        return get(target, p, null, cookies);
    }


    public MockMvcResult get(String target, Map<String, Object> p, Map<String, String> headers) {
        return get(target, p, headers, null);
    }

    public MockMvcResult get(String target, Map<String, Object> p, Map<String, String> headers, Set<Cookie> cookies) {
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

        if (cookies != null) {
            request.setCookies(cookies);
        }


        return doMockRequest(request);
    }

    public MockMvcResult post(String target) {
        return post(target, null, null, null, null);
    }

    public MockMvcResult post(String target, String postData) {
        return post(target, null, null, null, postData);
    }

    public MockMvcResult post(String target, Map<String, Object> paras) {
        return post(target, paras, null, null, null);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, String postData) {
        return post(target, paras, null, null, postData);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, Map<String, String> headers) {
        return post(target, paras, headers, null, null);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, Map<String, String> headers, String postData) {
        return post(target, paras, headers, null, postData);
    }

    public MockMvcResult post(String target, Map<String, Object> paras, Map<String, String> headers, Set<Cookie> cookies, String postData) {
        MockServletInputStream inStream = null;
        if (StrUtil.isNotBlank(postData)) {
            inStream = new MockServletInputStream(postData);
        }
        return doPost(target, paras, headers, cookies, inStream);
    }


    public MockMvcResult upload(String target, Map<String, Object> paras, Map<String, String> headers, Set<Cookie> cookies) {


        String endFlag = "\r\n";
        String startFlag = "--";
        String boundary = "------" + StrUtil.uuid();

        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (paras != null) {
            paras.forEach((key, value) -> {
                if (value instanceof File) {
                    File file = (File) value;
                    writeString(baos, startFlag + boundary + endFlag);
                    writeString(baos, "Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"");
                    writeString(baos, endFlag + "Content-Type: " + HttpMimeTypes.getMimeType(file.getName()));
                    writeString(baos, endFlag + endFlag);
                    writeFile(baos, file);
                    writeString(baos, endFlag);
                } else {
                    writeString(baos, startFlag + boundary + endFlag);
                    writeString(baos, "Content-Disposition: form-data; name=\"" + key + "\"");
                    writeString(baos, endFlag + endFlag);
                    writeString(baos, String.valueOf(value));
                    writeString(baos, endFlag);
                }
            });
        }

        writeString(baos, startFlag + boundary + startFlag + endFlag);
        return doPost(target, paras, headers, cookies, new MockServletInputStream(baos.toByteArray()));
    }

    private void writeFile(ByteArrayOutputStream dos, File file) {
        FileInputStream fStream = null;
        try {
            fStream = new FileInputStream(file);
            byte[] buffer = new byte[2028];
            for (int len = 0; (len = fStream.read(buffer)) > 0; ) {
                dos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fStream != null) {
                try {
                    fStream.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private void writeString(OutputStream dos, String s) {
        try {
            dos.write(s.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public MockMvcResult doPost(String target, Map<String, Object> paras, Map<String, String> headers, Set<Cookie> cookies, ServletInputStream inStream) {
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

        if (inStream != null) {
            request.setInputStream(inStream);
        }


        if (cookies != null) {
            request.setCookies(cookies);
        }

        return doMockRequest(request);
    }


    public MockMvcResult doMockRequest(MockHttpServletRequest request) {
        MockHttpServletResponse response = createResponse();
        try {
            if (requestStartListener != null) {
                requestStartListener.accept(request);
            }

            Set<Cookie> cookies = new HashSet<>();

            //开启 cookie 保持, 用户未设置自己的 cookie, 上次的 cookie 有值
            if (isHoldCookiesEnable()) {
                cookies.addAll(holdCookies);
            }

            for (Cookie cookie : request.getCookies()) {
                doSetCookie(cookie, cookies);
            }

            request.setCookies(cookies);
            doSendRequest(request, response);

        } finally {
            if (requestFinishedListener != null) {
                requestFinishedListener.accept(response);
            }

            if (isHoldCookiesEnable() && response.getCookies().size() > 0) {
                response.getCookies().forEach(cookie -> doSetCookie(cookie, holdCookies));
            }
        }

        return new MockMvcResult(response);
    }

    public void doSetCookie(Cookie newCookie, Set<Cookie> toCookies) {
        toCookies.removeIf(cookie -> Objects.equals(cookie.getName(), newCookie.getName()));
        if (StrUtil.isNotEmpty(newCookie.getValue())) {
            toCookies.add(newCookie);
        }
    }


    public void doSendRequest(MockHttpServletRequest request, MockHttpServletResponse response) {
        MockApp.mockRequest(request, response);
    }

    public MockHttpServletResponse createResponse() {
        return new MockHttpServletResponse();
    }

}
