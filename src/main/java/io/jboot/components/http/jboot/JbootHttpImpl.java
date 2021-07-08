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
package io.jboot.components.http.jboot;

import io.jboot.components.http.JbootHttp;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class JbootHttpImpl implements JbootHttp {


    @Override
    public JbootHttpResponse handle(JbootHttpRequest request) {

        JbootHttpResponse response = request.getDownloadFile() == null
                ? new JbootHttpResponse()
                : new JbootHttpResponse(request.getDownloadFile());
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


            //post 请求
            if (request.isPostRequest()) {

                connection.setDoOutput(true);

                //处理文件上传的post提交
                if (request.isMultipartFormData()) {
                    if (ArrayUtil.isNotEmpty(request.getParams())) {
                        uploadData(request, connection);
                    }
                }

                //处理正常的post提交
                else {
                    String postContent = request.getPostContent();
                    if (StrUtil.isNotEmpty(postContent)) {
                        try (OutputStream outStream = connection.getOutputStream();) {
                            outStream.write(postContent.getBytes(request.getCharset()));
                            outStream.flush();
                        }
                    }
                }
            }

            //get 请求
            else {
                connection.setInstanceFollowRedirects(request.isInstanceFollowRedirects());
                connection.connect();
            }


            inStream = getInputStream(connection);

            response.setContentType(connection.getContentType());
            response.setResponseCode(connection.getResponseCode());
            response.setHeaders(connection.getHeaderFields());

            //是否要读取 body 数据
            if (request.isReadBody()) {
                response.copyStream(inStream);
            }

        } catch (Throwable ex) {
            response.setError(ex);
        } finally {

            response.close();

            if (connection != null) {
                connection.disconnect();
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InputStream getInputStream(HttpURLConnection connection) throws IOException {

        InputStream stream = connection.getResponseCode() >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();

        if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
            return new GZIPInputStream(stream);
        } else {
            return stream;
        }

    }

    private void uploadData(JbootHttpRequest request, HttpURLConnection connection) throws IOException {
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
                writeString(dos, request, endFlag + endFlag);
                FileInputStream fStream = new FileInputStream(file);
                byte[] buffer = new byte[2028];
                for (int len = 0; (len = fStream.read(buffer)) > 0; ) {
                    dos.write(buffer, 0, len);
                }
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

        //如果 reqeust 的 header 不配置 content-Type, 使用默认的
        connection.setRequestProperty("Content-Type",request.getContentType());

        if (request.getHeaders() != null && request.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private static HttpURLConnection getConnection(JbootHttpRequest request) throws Exception {
        if (request.isPostRequest() == false) {
            request.initGetUrl();
        }
        return request.isHttps() ? getHttpsConnection(request) : getHttpConnection(request);
    }

    private static HttpURLConnection getHttpConnection(JbootHttpRequest request) throws Exception {
        URL url = new URL(request.getRequestUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    private static HttpsURLConnection getHttpsConnection(JbootHttpRequest request) throws Exception {
        URL url = new URL(request.getRequestUrl());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        if (request.getCertPath() != null && request.getCertPass() != null) {

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

        } else {
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
