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
package io.jboot.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class JbootHttpHandler implements IHttpHandler {


    @Override
    public HttpResponse handle(HttpRequest request) {

        return handle(request, null);

    }

    @Override
    public HttpResponse handle(HttpRequest request, File downloadToFile) {
        HttpURLConnection connection = null;
        InputStream stream = null;
        HttpResponse response = null;
        try {
            response = downloadToFile == null ? new HttpResponse() : new HttpResponse(downloadToFile);
            connection = request.getConnection();
            connection.connect();

            if (HttpRequest.METHOD_POST.equalsIgnoreCase(request.getMethod())) {
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

                byte[] bytes = null;
                do {
                    bytes = request.readContent();
                    if (bytes != null && bytes.length != 0) {
                        dos.write(bytes);
                    }
                } while (bytes != null);

            }

            stream = connection.getInputStream();
            response.pipe(stream);

        } catch (Throwable e) {
            response.setError(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (response != null) {
                response.finish();
            }
        }

        return response;
    }

}
