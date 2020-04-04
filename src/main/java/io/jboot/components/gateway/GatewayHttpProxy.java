/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.gateway;

import com.jfinal.log.Log;
import io.jboot.exception.JbootException;
import io.jboot.utils.StrUtil;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;


public class GatewayHttpProxy {

    private static final Log LOG = Log.getLog(GatewayHttpProxy.class);

    private int readTimeOut;
    private int connectTimeOut;
    private int retries;
    private String contentType;

    public GatewayHttpProxy(JbootGatewayConfig config) {
        this.readTimeOut = config.getProxyReadTimeout();
        this.connectTimeOut = config.getProxyConnectTimeout();
        this.retries = config.getProxyRetries();
        this.contentType = config.getProxyContentType();

    }

    public void sendRequest(String url, HttpServletRequest req, HttpServletResponse resp) {
        int triesCount = retries < 0 ? 0 : retries;
        Exception exception = null;

        do {
            try {
                exception = null;
                doSendRequest(url, req, resp);
            } catch (Exception ex) {
                exception = ex;
            }
        } while (exception != null && triesCount-- > 0);

        if (exception != null) {
            LOG.error(exception.toString(), exception);
        }
    }


    public void doSendRequest(String url, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        HttpURLConnection conn = null;
        try {
            conn = getConnection(url);

            /**
             * 设置 http 请求头
             */
            configConnection(conn, req);

            conn.setRequestMethod(req.getMethod());

            // get 请求
            if ("get".equalsIgnoreCase(req.getMethod())) {
                conn.connect();
            }
            // post 请求
            else {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                copyRequestStreamToConnection(req, conn);
            }


            /**
             * 配置响应的 HTTP 头
             */
            configResponse(resp, conn);

            /**
             * 复制目标相应流到 Response
             */
            copyStreamToResponse(conn, resp);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    private void copyRequestStreamToConnection(HttpServletRequest req, HttpURLConnection conn) throws IOException {
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = conn.getOutputStream();
            inStream = req.getInputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

        } finally {
            quetlyClose(outStream, inStream);
        }
    }


    private void copyStreamToResponse(HttpURLConnection conn, HttpServletResponse resp) throws IOException {
        InputStream inStream = null;
        InputStreamReader reader = null;
        try {
            if (!resp.isCommitted()) {
                PrintWriter writer = resp.getWriter();
                inStream = getInputStream(conn);
                reader = new InputStreamReader(inStream);
                int len;
                char[] buffer = new char[1024];
                while ((len = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, len);
                }
            }
        } finally {
            quetlyClose(inStream, reader);
        }
    }


    private static void quetlyClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }


    private void configResponse(HttpServletResponse resp, HttpURLConnection conn) throws IOException {
        resp.setContentType(contentType);
        resp.setStatus(conn.getResponseCode());

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        if (headerFields != null && !headerFields.isEmpty()) {
            Set<String> headerNames = headerFields.keySet();
            for (String headerName : headerNames) {
                //需要排除 Content-Encoding，因为 Server 可能已经使用 gzip 压缩，但是此代理已经对 gzip 内容进行解压了
                if (StrUtil.isNotBlank(headerName) && !"Content-Encoding".equalsIgnoreCase(headerName)) {
                    resp.setHeader(headerName, conn.getHeaderField(headerName));
                }
            }
        }
    }

    private static InputStream getInputStream(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
            return new GZIPInputStream(stream);
        } else {
            return stream;
        }
    }


    private void configConnection(HttpURLConnection conn, HttpServletRequest req) throws ProtocolException {

        conn.setReadTimeout(readTimeOut);
        conn.setConnectTimeout(connectTimeOut);
        conn.setInstanceFollowRedirects(false);
        conn.setUseCaches(false);

        conn.setRequestMethod(req.getMethod());
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (StrUtil.isNotBlank(headerName)) {
                conn.setRequestProperty(headerName, req.getHeader(headerName));
            }
        }
    }

    private static HttpURLConnection getConnection(String urlString) {
        try {
            if (urlString.toLowerCase().startsWith("https")) {
                return getHttpsConnection(urlString);
            } else {
                return getHttpConnection(urlString);
            }
        } catch (Throwable ex) {
            throw new JbootException(ex);
        }
    }

    private static HttpURLConnection getHttpConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    private static HttpsURLConnection getHttpsConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(hnv);
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        if (sslContext != null) {
            TrustManager[] tm = {trustAnyTrustManager};
            sslContext.init(null, tm, null);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            conn.setSSLSocketFactory(ssf);
        }
        return conn;
    }

    private static X509TrustManager trustAnyTrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static HostnameVerifier hnv = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


}
