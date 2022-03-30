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
package io.jboot.components.http.okhttp;

import io.jboot.components.http.JbootHttp;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class OKHttpImpl implements JbootHttp {

    public OKHttpImpl() {

    }

    @Override
    public JbootHttpResponse handle(JbootHttpRequest request) {

        JbootHttpResponse response = request.getDownloadFile() == null
                ? new JbootHttpResponse()
                : new JbootHttpResponse(request.getDownloadFile());
        doProcess(request, response);

        return response;
    }


    private void doProcess(JbootHttpRequest request, JbootHttpResponse response) {
        try {

            // post 请求 或者 put 请求
            if (request.isPostRequest() || request.isPutRequest()) {
                doProcessPostRequest(request, response);
            }

            // 其他非 post 和 put 请求
            else {
                request.appendParasToUrl();
                doProcessGetRequest(request, response);
            }

        } catch (Throwable ex) {
            response.setError(ex);
        } finally {
            response.close();
        }
    }

    private void doProcessGetRequest(JbootHttpRequest request, JbootHttpResponse response) throws Exception {
        Request okHttpRequest = new Request.Builder()
                .url(request.getRequestUrl())
                .build();


        doProcessRequest(request, response, okHttpRequest);
    }

    private void doProcessPostRequest(final JbootHttpRequest request, JbootHttpResponse response) throws Exception {
        RequestBody requestBody = null;
        if (request.isMultipartFormData()) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                } else {
                    builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
                }
            }
            requestBody = builder.build();
        } else {
//            FormBody.Builder builder = new FormBody.Builder();
//            for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
//                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
//            }
//            requestBody = builder.build();

            MediaType mediaType = MediaType.parse(request.getContentType());
            requestBody = RequestBody.create(mediaType, request.getPostContent());
        }


        Request okHttpRequest = new Request.Builder().url(request.getRequestUrl())
                .post(requestBody)
                .build();


        doProcessRequest(request, response, okHttpRequest);
    }

    private void doProcessRequest(JbootHttpRequest request, JbootHttpResponse response, Request okHttpRequest) throws Exception {
        OkHttpClient client = getClient(request);
        Call call = client.newCall(okHttpRequest);
        Response okHttpResponse = call.execute();
        response.setResponseCode(okHttpResponse.code());
        response.setContentType(okHttpResponse.body().contentType().type());

        if (request.isReadBody()) {
            response.copyStream(okHttpResponse.body().byteStream());
        }
    }


    private OkHttpClient getClient(JbootHttpRequest request) throws Exception {
        if (request.getRequestUrl().toLowerCase().startsWith("https")) {
            return getHttpsClient(request);
        }


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (request.getProxy() != null) {
            builder.proxy(request.getProxy());
        }

        return builder.build();
    }

    public OkHttpClient getHttpsClient(JbootHttpRequest request) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //自定义 sslContext
        if (request.getSslContext() != null) {
            SSLSocketFactory ssf = request.getSslContext().getSocketFactory();
            builder.sslSocketFactory(ssf, trustAnyTrustManager);
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

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();


            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());

            X509TrustManager x509TrustManager = trustAnyTrustManager;
            if (trustManagers != null && trustManagers.length > 0 && trustManagers[0] instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManagers[0];
            }

            builder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);

        } else {
            builder.hostnameVerifier(hnv);
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            if (sslContext != null) {
                TrustManager[] trustManagers = {trustAnyTrustManager};
                sslContext.init(null, trustManagers, new SecureRandom());
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustAnyTrustManager);
            }
        }

        return builder.build();
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
            return new X509Certificate[0];
        }
    };

    private static HostnameVerifier hnv = (hostname, session) -> true;
}
