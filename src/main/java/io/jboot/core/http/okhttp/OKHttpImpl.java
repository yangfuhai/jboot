/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.http.okhttp;

import com.jfinal.log.Log;
import io.jboot.core.http.JbootHttpBase;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.core.http.okhttp
 */
public class OKHttpImpl extends JbootHttpBase {
    private static final Log LOG = Log.getLog(OKHttpImpl.class);

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
            if (request.isPostRquest()) {
                doProcessPostRequest(request, response);
            }

            /**
             * get 请求
             */
            else if (request.isGetRquest()) {
                buildGetUrlWithParams(request);
                doProcessGetRequest(request, response);
            }

        } catch (Throwable ex) {
            LOG.error(ex.toString(), ex);
            response.setError(ex);
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
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
            requestBody = builder.build();
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
        response.pipe(okHttpResponse.body().byteStream());
        response.finish();
    }


    private OkHttpClient getClient(JbootHttpRequest request) throws Exception {
        if (request.getRequestUrl().toLowerCase().startsWith("https")) {
            return getHttpsClient(request);
        }

        return new OkHttpClient();
    }

    public OkHttpClient getHttpsClient(JbootHttpRequest request) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (request.getCertPath() != null && request.getCertPass() != null) {
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(request.getCertPath()), request.getCertPass().toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientStore, request.getCertPass().toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();


            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();


            SSLContext sslContext = SSLContext.getInstance("TLSv1");
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
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static HostnameVerifier hnv = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
