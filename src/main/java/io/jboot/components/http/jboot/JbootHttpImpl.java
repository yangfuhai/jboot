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
package io.jboot.components.http.jboot;

import com.jfinal.log.Log;
import io.jboot.components.http.HttpMimeTypes;
import io.jboot.components.http.JbootHttp;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.QuietlyUtil;
import io.jboot.utils.StrUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class JbootHttpImpl implements JbootHttp {

    private static final Log LOG = Log.getLog(JbootHttpImpl.class);


    @Override
    public JbootHttpResponse handle(JbootHttpRequest request) {
        JbootHttpResponse response = new JbootHttpResponse(request);
        doProcess(request, response);
        return response;
    }


    private void doProcess(JbootHttpRequest request, JbootHttpResponse response) {
        HttpURLConnection connection = null;
        InputStream inStream = null;
        try {

            //获取 http 链接
            connection = getConnection(request);

            //配置 http 链接
            configConnection(connection, request);


            //post 或者 put 请求
            if (request.isPostRequest() || request.isPutRequest()) {

                connection.setDoOutput(true);

                //处理文件上传的post提交
                if (request.isMultipartFormData()) {
                    if (ArrayUtil.isNotEmpty(request.getParams())) {
                        uploadByMultipart(request, connection);
                    }
                }

                //处理正常的post提交
                else {
                    String uploadBodyString = request.getUploadBodyString();
                    if (StrUtil.isNotEmpty(uploadBodyString)) {
                        byte[] bytes = uploadBodyString.getBytes(request.getCharset());

                        if (StrUtil.isBlank(request.getHeader("Content-Length"))) {
                            connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                        }

                        try (OutputStream outStream = connection.getOutputStream();) {
                            outStream.write(bytes);
                            outStream.flush();
                        }
                    }
                }
            }

            //get 请求
            else {
                connection.connect();
            }

            int responseCode = connection.getResponseCode();

            //自动重定向
            if (responseCode >= 300 && responseCode < 400 && request.isAutoRedirect()) {
                processRedirect(request, response, connection);
                return;
            }


            inStream = getInputStream(connection, responseCode);

            response.setContentType(connection.getContentType());
            response.setResponseCode(connection.getResponseCode());
            response.setHeaders(connection.getHeaderFields());

            //是否要读取 body 数据
            if (request.isReadBody()) {
                response.copyStream(inStream);
            }

        } catch (Throwable ex) {
            response.setError(ex);
            LOG.error(ex.toString(), ex);
        } finally {

            if (connection != null) {
                connection.disconnect();
            }

            QuietlyUtil.closeQuietly(inStream, response);
        }
    }


    /**
     * 手动重定向
     *
     * @param request
     * @param response
     * @param connection
     */
    private void processRedirect(JbootHttpRequest request, JbootHttpResponse response, HttpURLConnection connection) throws IOException {
        if (request.getCurrentRedirectCount() > request.getMaxRedirectCount()) {
            throw new IOException("Exceeded redirect count.");
        }


        String location = connection.getHeaderField("Location");
        request.setCurrentRedirectCount(request.getCurrentRedirectCount() + 1);

        //绝对路径
        if (location.startsWith("/")) {
            int firstSlash = request.getRequestUrl().indexOf("/", 8); // 8  == "https://".length()
            location = request.getRequestUrl().substring(0, firstSlash) + location;
        }

        //相对路径
        else if (!location.toLowerCase().startsWith("http")) {
            int lastSlash = request.getRequestUrl().lastIndexOf("/");
            location = request.getRequestUrl().substring(0, lastSlash + 1) + location;
        }

        //携带 cookie
        String responseCookieString = connection.getHeaderField("Set-Cookie");
        if (StrUtil.isNotBlank(responseCookieString)) {
            List<HttpCookie> cookies = HttpCookie.parse(responseCookieString);
            StringBuilder cookie = new StringBuilder(StrUtil.obtainDefault(request.getHeader("Cookie"), ""));
            for (HttpCookie httpCookie : cookies) {
                cookie.append(httpCookie.getName()).append("=").append(httpCookie.getValue()).append("; ");
            }
            request.addHeader("Cookie", cookie.toString());
        }

        request.setRequestUrl(location);
        request.setMethod(JbootHttpRequest.METHOD_GET);

        doProcess(request, response);
    }


    private InputStream getInputStream(HttpURLConnection connection, int responseCode) throws IOException {
        InputStream stream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
            return new GZIPInputStream(stream);
        } else {
            return stream;
        }

    }


    private void uploadByMultipart(JbootHttpRequest request, HttpURLConnection connection) throws IOException {
        String endFlag = "\r\n";
        String startFlag = "--";
        String boundary = "------" + StrUtil.uuid();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        for (Map.Entry entry : request.getParams().entrySet()) {
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                checkFileNormal(file);
                writeString(dos, request, startFlag + boundary + endFlag);
                writeString(dos, request, "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + file.getName() + "\"");
                writeString(dos, request, endFlag + "Content-Type: " + HttpMimeTypes.getMimeType(file.getName()));
                writeString(dos, request, endFlag + endFlag);

                writeFile(dos, file);

                writeString(dos, request, endFlag);
            } else {
                writeString(dos, request, startFlag + boundary + endFlag);
                writeString(dos, request, "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
                writeString(dos, request, endFlag + endFlag);
                writeString(dos, request, String.valueOf(entry.getValue()));
                writeString(dos, request, endFlag);
            }
        }

        writeString(dos, request, startFlag + boundary + startFlag + endFlag);
        dos.flush();
    }

    private void writeString(DataOutputStream dos, JbootHttpRequest request, String s) throws IOException {
        dos.write(s.getBytes(request.getCharset()));
    }

    private void writeFile(DataOutputStream dos, File file) throws IOException {
        try (FileInputStream fStream = new FileInputStream(file)) {
            byte[] buffer = new byte[2028];
            for (int len = 0; (len = fStream.read(buffer)) > 0; ) {
                dos.write(buffer, 0, len);
            }
        }
    }

    private static void checkFileNormal(File file) {
        if (!file.exists()) {
            throw new JbootException("file not exists!!!!" + file);
        }
        if (file.isDirectory()) {
            throw new JbootException("cannot upload directory!!!!" + file);
        }
        if (!file.canRead()) {
            throw new JbootException("cannnot read file!!!" + file);
        }
    }


    private static void configConnection(HttpURLConnection connection, JbootHttpRequest request) throws ProtocolException {
        if (connection == null) {
            return;
        }
        connection.setReadTimeout(request.getReadTimeOut());
        connection.setConnectTimeout(request.getConnectTimeOut());
        connection.setRequestMethod(request.getMethod());
        connection.setInstanceFollowRedirects(request.isInstanceFollowRedirects());

        //如果 reqeust 的 header 不配置 content-Type, 使用默认的
        connection.setRequestProperty("Content-Type", request.getContentType());

        if (request.getHeaders() != null && request.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private static HttpURLConnection getConnection(JbootHttpRequest request) throws Exception {

        //get 请求 或者 带有body内容的 post 请求，需要在 url 追加参数
        if (!request.isPostOrPutRequest() || request.getBodyContent() != null) {
            request.appendParasToUrl();
        }

        return request.isHttps() ? getHttpsConnection(request) : getHttpConnection(request);
    }

    private static HttpURLConnection getHttpConnection(JbootHttpRequest request) throws Exception {
        URL url = new URL(request.getRequestUrl());
        HttpURLConnection conn = (HttpURLConnection) (request.getProxy() != null
                ? url.openConnection(request.getProxy()) : url.openConnection());
        return conn;
    }

    private static HttpsURLConnection getHttpsConnection(JbootHttpRequest request) throws Exception {
        URL url = new URL(request.getRequestUrl());
        HttpsURLConnection conn = (HttpsURLConnection) (request.getProxy() != null
                ? url.openConnection(request.getProxy()) : url.openConnection());

        //自定义 sslContext
        if (request.getSslContext() != null) {
            SSLSocketFactory ssf = request.getSslContext().getSocketFactory();
            conn.setSSLSocketFactory(ssf);
        }
        //配置证书的路径和密码
        else if (request.getCertPath() != null && request.getCertPass() != null) {

            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(request.getCertInputStream(), request.getCertPass().toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientStore, request.getCertPass().toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();


            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());

            conn.setSSLSocketFactory(sslContext.getSocketFactory());

        }
        // 默认的 sslContext
        else {
            conn.setHostnameVerifier(hnv);
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            if (sslContext != null) {
                TrustManager[] tm = {trustAnyTrustManager};
                sslContext.init(null, tm, null);
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                conn.setSSLSocketFactory(ssf);
            }
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

    private static HostnameVerifier hnv = (hostname, session) -> true;


}
