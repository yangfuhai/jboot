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
package io.jboot.components.http;

import io.jboot.utils.StrUtil;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * httpRequest
 */
public class JbootHttpRequest {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_TRACE = "TRACE";

    public static final int READ_TIME_OUT = 1000 * 10; // 10秒
    public static final int CONNECT_TIME_OUT = 1000 * 5; // 5秒
    public static final String CHAR_SET = "UTF-8";

    public static final String CONTENT_TYPE_TEXT = "text/plain; charset=utf-8";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded; charset=utf-8";

    private String requestUrl;

    private Map<String, String> headers;
    private Map<String, Object> params;


    private String method = METHOD_GET;
    private String charset = CHAR_SET;

    private boolean multipartFormData = false;

    private String certPath;
    private String certPass;

    private int readTimeOut;
    private int connectTimeOut;
    private String contentType;


    private File downloadFile;
    private String bodyContent;

    // 如果某些时候只是为了去读取 http 头信息，而不需要 http body，可以配置为 false
    private boolean readBody = true;

    // 遇到重定向是否自动跟随
    private boolean instanceFollowRedirects = false;

    // 自定义 sslContext
    private SSLContext sslContext;

    // 自定义 http 代理
    private HttpProxyInfo httpProxyInfo;

    // 是否自动重定向（手动实现的，而非）
    private boolean autoRedirect = true;

    // 最大的重定向次数
    private int maxRedirectCount = 3;

    // 当前的重定向次数
    private int currentRedirectCount = 0;


    public static JbootHttpRequest create(String url) {
        return new JbootHttpRequest(url);
    }

    public static JbootHttpRequest create(String url, String method) {
        JbootHttpRequest request = new JbootHttpRequest(url);
        request.setMethod(method);
        return request;
    }

    public static JbootHttpRequest create(String url, Map<String, Object> params) {
        JbootHttpRequest request = new JbootHttpRequest(url);
        request.setParams(params);
        return request;
    }

    public static JbootHttpRequest create(String url, Map<String, Object> params, String method) {
        JbootHttpRequest request = new JbootHttpRequest(url);
        request.setMethod(method);
        request.setParams(params);
        return request;
    }

    public JbootHttpRequest() {
        JbootHttpConfig config = JbootHttpManager.me().getHttpConfig();

        if (StrUtil.isNotBlank(config.getCertPath())) {
            this.certPath = config.getCertPath();
        }
        if (StrUtil.isNotBlank(config.getCertPass())) {
            this.certPass = config.getCertPass();
        }

        this.readTimeOut = config.getReadTimeOut();
        this.connectTimeOut = config.getConnectTimeOut();
        this.contentType = config.getContentType();
        this.httpProxyInfo = config.getHttpProxyInfo();
    }

    public JbootHttpRequest(String url) {
        this();
        this.requestUrl = url;
    }


    public void addParam(String key, Object value) {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        if (value instanceof File) {
            setMultipartFormData(true);
        }
        params.put(key, value);
    }

    public void addParams(Map<String, Object> map) {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof File) {
                setMultipartFormData(true);
            }

            params.put(entry.getKey(), entry.getValue());
        }
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getCertPath() {
        return certPath;
    }

    public InputStream getCertInputStream() throws FileNotFoundException {
        if (StrUtil.isBlank(certPath)) {
            return null;
        }

        if (certPath.toLowerCase().startsWith("classpath:")) {

            String path = certPath.substring(10).trim();
            InputStream inStream = getClassLoader().getResourceAsStream(path);

            if (inStream == null) {
                inStream = getClassLoader().getResourceAsStream("webapp/" + path);
            }

            if (inStream == null) {
                throw new FileNotFoundException("Can not load resource: " + path + " in classpath.");
            } else {
                return inStream;
            }
        } else {
            return new FileInputStream(certPath);
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : getClass().getClassLoader();
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name){
        return headers != null ? headers.get(name) : null;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.putAll(headers);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        if (params == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof File) {
                setMultipartFormData(true);
            }
        }
        this.params = params;
    }

    public boolean isPostRequest() {
        return METHOD_POST.equalsIgnoreCase(method);
    }

    public boolean isPutRequest() {
        return METHOD_PUT.equalsIgnoreCase(method);
    }

    public boolean isPostOrPutRequest() {
        return isPostRequest() || isPutRequest();
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isMultipartFormData() {
        return multipartFormData;
    }

    public void setMultipartFormData(boolean multipartFormData) {
        this.multipartFormData = multipartFormData;
    }

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getBodyContent() {
        return bodyContent;
    }

    public String getUploadBodyString() {
        if (bodyContent != null) {
            return bodyContent;
        } else {
            return buildParams();
        }
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }


    private String buildParams() {
        return StrUtil.mapToQueryString(getParams());
    }


    public void appendParasToUrl() {
        String params = buildParams();

        if (StrUtil.isBlank(params)) {
            return;
        }

        String originUrl = getRequestUrl();
        if (originUrl.contains("?")) {
            originUrl = originUrl + "&" + params;
        } else {
            originUrl = originUrl + "?" + params;
        }

        setRequestUrl(originUrl);
    }


    public boolean isHttps() {
        return requestUrl != null && requestUrl.toLowerCase().startsWith("https");
    }

    public boolean isReadBody() {
        return readBody;
    }

    public void setReadBody(boolean readBody) {
        this.readBody = readBody;
    }

    public boolean isInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    public void setInstanceFollowRedirects(boolean instanceFollowRedirects) {
        this.instanceFollowRedirects = instanceFollowRedirects;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public HttpProxyInfo getHttpProxyInfo() {
        return httpProxyInfo;
    }

    public void setHttpProxyInfo(HttpProxyInfo httpProxyInfo) {
        this.httpProxyInfo = httpProxyInfo;
    }

    public Proxy getProxy() {
        return httpProxyInfo != null ? httpProxyInfo.getProxy() : null;
    }

    public boolean isAutoRedirect() {
        return autoRedirect;
    }

    public void setAutoRedirect(boolean autoRedirect) {
        this.autoRedirect = autoRedirect;
    }

    public int getMaxRedirectCount() {
        return maxRedirectCount;
    }

    public void setMaxRedirectCount(int maxRedirectCount) {
        this.maxRedirectCount = maxRedirectCount;
    }

    public int getCurrentRedirectCount() {
        return currentRedirectCount;
    }

    public void setCurrentRedirectCount(int currentRedirectCount) {
        this.currentRedirectCount = currentRedirectCount;
    }
}
