package io.jboot.core.http.okhttp;

import io.jboot.core.http.JbootHttpResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.core.http.okhttp
 */
public class OkHttpResponseCallback implements Callback {


    private JbootHttpResponse jbootResponse;

    OkHttpResponseCallback(JbootHttpResponse response) {
        this.jbootResponse = response;
    }


    @Override
    public void onFailure(Call call, IOException e) {
        jbootResponse.setError(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        jbootResponse.pipe(response.body().byteStream());
        jbootResponse.finish();
        jbootResponse.setResponseCode(response.code());
        jbootResponse.setContentType(response.body().contentType().type());
    }
}
