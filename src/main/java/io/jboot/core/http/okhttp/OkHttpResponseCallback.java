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
