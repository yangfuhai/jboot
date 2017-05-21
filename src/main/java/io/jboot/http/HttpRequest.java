/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.http;

import javax.net.ssl.*;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * httpRequest
 */
public class HttpRequest {

    public static final String METHOD_POST = "post";
    public static final String METHOD_GET = "get";

    private static final String CHAR_SET = "UTF-8";

    Map<String, String> headers = new HashMap<>();
    Map<String, Object> params = new HashMap<>();

    private String requestUrl;
    private String certPath;
    private String certPass;

    private String method = METHOD_GET;

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.requestUrl = url;
    }


    public static HttpRequest createWithdDefaultHeaders(String requestUrl) {
        HttpRequest request = new HttpRequest(requestUrl);
        request.headers.put("Content-Type", "application/x-www-form-urlencoded");
        request.headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        return request;
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    public void addParam(String key, Object value) {
        params.put(key, value);
    }


    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getCertPath() {
        return certPath;
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

    /**
     * 添加http头
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }


    boolean readBefore = false;

    public byte[] readContent() {
        if (!METHOD_POST.equalsIgnoreCase(method)) {
            return null;
        }

        if (readBefore) {
            return null;
        }

        readBefore = true;
        return buildParamAsString().getBytes();
    }


    public HttpURLConnection getConnection() throws Exception {
        HttpURLConnection conn = getConnection(getRequestUrl());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        return conn;
    }


    private String getRequestUrl() {
        if (METHOD_GET.equalsIgnoreCase(method)) {
            if (!requestUrl.contains("?")) {
                requestUrl += "?";
            }

            if (requestUrl.charAt(requestUrl.length() - 1) != '?') {
                requestUrl += "&";
            }

            requestUrl += buildParamAsString();
        }


        return requestUrl;
    }


    /**
     * 把http参数构建成文本内容
     *
     * @return
     */
    public String buildParamAsString() {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getValue().toString().trim().length() != 0)
                try {
                    builder.append(entry.getKey().trim())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), CHAR_SET))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }


    private static HttpURLConnection getConnection(String strUrl) throws Exception {
        if (strUrl == null) {
            return null;
        }
        if (strUrl.toLowerCase().startsWith("https")) {
            return getHttpsConnection(strUrl);
        } else {
            return getHttpConnection(strUrl);
        }
    }

    private static HttpURLConnection getHttpConnection(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    private static HttpsURLConnection getHttpsConnection(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(hnv);
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        if (sslContext != null) {
            TrustManager[] tm = {xtm};
            sslContext.init(null, tm, null);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            conn.setSSLSocketFactory(ssf);
        }

        return conn;
    }

    private static X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static HostnameVerifier hnv = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


}
