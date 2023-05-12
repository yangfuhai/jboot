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
package io.jboot.test.web;

import com.jfinal.core.JFinal;
import io.jboot.test.MockProxy;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.*;

public class MockHttpServletResponse extends HttpServletResponseWrapper {

    protected String contentString;
    protected ByteArrayOutputStream stream = new ByteArrayOutputStream();

    protected PrintWriter writer;
    protected Map<String, String> headers = new HashMap<>();
    protected Set<Cookie> cookies = new HashSet<>();

    protected int status = 200;
    protected String statusMessage = "OK";
    protected Locale locale;
    protected String contentType;
    protected String characterEncoding = JFinal.me().getConstants().getEncoding();


    public MockHttpServletResponse() {
        super(MockProxy.create(HttpServletResponse.class));
    }

    public MockHttpServletResponse(ByteArrayOutputStream stream) {
        super(MockProxy.create(HttpServletResponse.class));
        this.stream = stream;
    }

    public MockHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    public MockHttpServletResponse(HttpServletResponse response, ByteArrayOutputStream stream) {
        super(response);
        this.stream = stream;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }


    @Override
    public void addDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    @Override
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void addIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    @Override
    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    @Override
    public void sendError(int status) throws IOException {
        this.setStatus(status);
    }

    @Override
    public void sendError(int status, String statusMessage) throws IOException {
        this.setStatus(status, statusMessage);
    }

    @Override
    public void sendRedirect(String value) throws IOException {
        if (status == 200) {
            setStatus(302);
        }
        headers.put("Location", value);
    }

    @Override
    public void setDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    @Override
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void setIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Override
    public void flushBuffer() throws IOException {
        getWriter().flush();
    }

    @Override
    public int getBufferSize() {
        return stream.size();
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {

            @Override
            public void write(int arg0) throws IOException {
                stream.write(arg0);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, characterEncoding));
        }
        return writer;
    }

    @Override
    public void reset() {
        stream.reset();
    }

    @Override
    public void resetBuffer() {
        stream.reset();
    }


    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getContentString() {
        if (contentString == null) {
            try {
                getWriter().flush();
                contentString = stream.toString(characterEncoding);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return contentString;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

}
