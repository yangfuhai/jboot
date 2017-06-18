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
package io.jboot.core.http.jboot;

import io.jboot.exception.JbootException;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jboot.core.http.JbootHttp;
import io.jboot.utils.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;


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
        InputStream stream = null;
        try {

            connection = getConnection(request);
            configConnection(connection, request);


            if (request.isGetRquest()) {
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                if (connection.getResponseCode() >= 400) {
                    stream = connection.getErrorStream();
                } else {
                    stream = connection.getInputStream();
                }
            }
            /**
             * 处理 post请求
             */
            else if (request.isPostRquest()) {

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                if (!request.isMultipartFormData()) {
                    String postContent = buildParams(request);
                    if (StringUtils.isNotEmpty(postContent)) {
                        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                        dos.write(postContent.getBytes(request.getCharset()));
                        dos.flush();
                        dos.close();
                    }
                    stream = connection.getInputStream();
                }

                /**
                 * 处理文件上传
                 */
                else {

                    if (request.getParams() != null && request.getParams().size() > 0) {
                        String endFlag = "\r\n";
                        String boundary = "---------" + StringUtils.uuid();
                        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                        for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
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
                }
            }

            response.setContentType(connection.getContentType());
            response.setResponseCode(connection.getResponseCode());
            response.setHeaders(connection.getHeaderFields());

            response.pipe(stream);
            response.finish();


        } catch (Throwable ex) {
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


    public static String buildParams(JbootHttpRequest request) throws UnsupportedEncodingException {
        Map<String, Object> params = request.getParams();
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
            if (entry.getKey() != null && StringUtils.isNotBlank(entry.getValue()))
                builder.append(entry.getKey().trim()).append("=")
                        .append(URLEncoder.encode(entry.getValue().toString(), request.getCharset())).append("&");
        }

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
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
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, request.getCertPass().toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(kms, null, new SecureRandom());
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

        } else {
            conn.setHostnameVerifier(hnv);
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            if (sslContext != null) {
                TrustManager[] tm = {xtm};
                sslContext.init(null, tm, null);
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                conn.setSSLSocketFactory(ssf);
            }
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
