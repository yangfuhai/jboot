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
import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GatewayUtil {

    private static final Log LOG = Log.getLog(GatewayUtil.class);

    private static int READ_TIMEOUT = 10000;
    private static int CONNECT_TIMEOUT = 5000;


    public static void sendRequest(String url, HttpServletRequest request, HttpServletResponse response) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            connection = getConnection(url);

            configConnection(connection, request);

            connection.connect();

            inputStream = getInutStream(connection);
            outputStream = connection.getOutputStream();

            IOUtils.copy(inputStream, outputStream);

            configResponse(response,connection);
        } catch (Exception ex) {
            LOG.warn(ex.toString(), ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            quetlyClose(inputStream,outputStream);
        }
    }


    private static void quetlyClose(Closeable ... closeables){
        for (Closeable closeable : closeables){
            if (closeable != null){
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void configResponse(HttpServletResponse response, HttpURLConnection connection) throws IOException {
        response.setContentType(connection.getContentType());
        response.setStatus(connection.getResponseCode());

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (headerFields != null && !headerFields.isEmpty()){
            Set<String> headerNames = headerFields.keySet();
            for (String headerName : headerNames){
                response.setHeader(headerName,connection.getHeaderField(headerName));
            }
        }
    }

    private static InputStream getInutStream(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();
    }



    private static void configConnection(HttpURLConnection connection, HttpServletRequest request) throws ProtocolException {

        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setInstanceFollowRedirects(true);

        connection.setRequestMethod(request.getMethod());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            connection.setRequestProperty(headerName,request.getHeader(headerName));
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

    private static HttpURLConnection getHttpConnection(String urlStr) throws Exception {
        URL url = new URL(urlStr);
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
