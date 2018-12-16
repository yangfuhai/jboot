/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.http.jboot;

import com.jfinal.log.Log;
import io.jboot.core.http.JbootHttp;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jboot.exception.JbootException;
import io.jboot.kits.ArrayKits;
import io.jboot.kits.StringKits;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;


public class JbootHttpImpl implements JbootHttp {

    private static final Log LOG = Log.getLog(JbootHttpImpl.class);


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
        InputStream stream = null;
        try {

            connection = getConnection(request);
            configConnection(connection, request);


            if (request.isPostRequest() == false) {

                connection.setInstanceFollowRedirects(true);
                connection.connect();
            }
            /**
             * 处理 post请求
             */
            else if (request.isPostRequest()) {

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                /**
                 * 处理 非文件上传的 post 请求
                 */
                if (!request.isMultipartFormData()) {

                    String postContent = request.getPostContent();
                    if (StringKits.isNotEmpty(postContent)) {
                        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                        dos.write(postContent.getBytes(request.getCharset()));
                        dos.flush();
                        dos.close();
                    }

                }

                /**
                 * 处理文件上传的post请求
                 */
                else {

                    if (ArrayKits.isNotEmpty(request.getParams())) {
                        uploadData(request, connection);
                    }

                }
            }

            stream = getInutStream(connection);

            response.setContentType(connection.getContentType());
            response.setResponseCode(connection.getResponseCode());
            response.setHeaders(connection.getHeaderFields());

            response.pipe(stream);
            response.finish();

        } catch (Throwable ex) {
            LOG.warn(ex.toString(), ex);
            response.setError(ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InputStream getInutStream(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
    }

    private void uploadData(JbootHttpRequest request, HttpURLConnection connection) throws IOException {
        String endFlag = "\r\n";
        String boundary = "---------" + StringKits.uuid();
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        for (Map.Entry entry : request.getParams().entrySet()) {
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                checkFileNormal(file);
                dos.writeBytes(boundary + endFlag);
                dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", entry.getKey(), file.getName()) + endFlag);
                dos.writeBytes(endFlag);
                FileInputStream fStream = new FileInputStream(file);
                byte[] buffer = new byte[2028];
                for (int len = 0; (len = fStream.read(buffer)) > 0; ) {
                    dos.write(buffer, 0, len);
                }

                dos.writeBytes(endFlag);
            } else {
                dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
                dos.writeBytes(endFlag);
                dos.writeBytes(endFlag);
                dos.writeBytes(String.valueOf(entry.getValue()));
                dos.writeBytes(endFlag);
            }
        }

        dos.writeBytes("--" + boundary + "--" + endFlag);
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
        if (connection == null)
            return;
        connection.setReadTimeout(request.getReadTimeOut());
        connection.setConnectTimeout(request.getConnectTimeOut());
        connection.setRequestMethod(request.getMethod());


        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (request.getHeaders() != null && request.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private static HttpURLConnection getConnection(JbootHttpRequest request) {
        try {
            if (request.isPostRequest() == false) {
                request.initGetUrl();
            }
            if (request.getRequestUrl().toLowerCase().startsWith("https")) {
                return getHttpsConnection(request);
            } else {
                return getHttpConnection(request.getRequestUrl());
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

    private static HttpsURLConnection getHttpsConnection(JbootHttpRequest request) throws Exception {
        URL url = new URL(request.getRequestUrl());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        if (request.getCertPath() != null && request.getCertPass() != null) {

            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(request.getCertPath()), request.getCertPass().toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientStore, request.getCertPass().toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();


            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientStore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");
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
