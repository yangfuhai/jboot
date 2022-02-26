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
package io.jboot.web.cached;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class CacheSupportResponseProxy implements HttpServletResponse {

    final private HttpServletResponse proxy;
    private CacheSupportWriter cacheWriter;
    private String cacheName;
    private String cacheKey;
    private int cacheLiveSeconds;


    public CacheSupportResponseProxy(HttpServletResponse proxy) {
        this.proxy = proxy;
    }

    @Override
    public void addCookie(Cookie cookie) {
        proxy.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return proxy.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return proxy.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return proxy.encodeRedirectURL(url);
    }

    @Override
    public String encodeUrl(String url) {
        return proxy.encodeUrl(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return proxy.encodeRedirectUrl(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        proxy.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        proxy.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        proxy.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        proxy.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        proxy.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        proxy.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        proxy.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        proxy.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        proxy.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
        proxy.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        proxy.setStatus(sc, sm);
    }

    @Override
    public int getStatus() {
        return proxy.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return proxy.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return proxy.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return proxy.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding() {
        return proxy.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return proxy.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return proxy.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (cacheWriter == null) {
            cacheWriter = new CacheSupportWriter(proxy.getWriter());
        }
        return cacheWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        proxy.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        proxy.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        proxy.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        proxy.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        proxy.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return proxy.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        proxy.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        proxy.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return proxy.isCommitted();
    }

    @Override
    public void reset() {
        proxy.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        proxy.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return proxy.getLocale();
    }

    public String getResponseString() {
        return cacheWriter != null ? cacheWriter.getWriterString() : null;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public int getCacheLiveSeconds() {
        return cacheLiveSeconds;
    }

    public void setCacheLiveSeconds(int cacheLiveSeconds) {
        this.cacheLiveSeconds = cacheLiveSeconds;
    }
}
