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

import com.jfinal.log.Log;

import java.io.*;

public class HttpResponse {
    private static final Log log = Log.getLog(HttpResponse.class);

    private OutputStream outputStream;
    private File file;
    private Throwable error;

    public HttpResponse() {
        this.outputStream = new ByteArrayOutputStream();
    }

    public HttpResponse(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            this.file = file;
            this.outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            setError(e);
        }
    }


    /**
     * 获取数据内容
     *
     * @return
     */
    public String getContentAsString() {
        if (outputStream != null && outputStream instanceof ByteArrayOutputStream) {
            return new String(((ByteArrayOutputStream) outputStream).toByteArray());
        }
        return null;
    }


    /**
     * 把 inputStream 写入response
     *
     * @param inputStream
     */
    public void pipe(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = inputStream.read(buffer)) > 0; ) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Throwable throwable) {
            log.error(throwable.toString(), throwable);
        }
    }

    /**
     * 结束response和释放资源
     */
    public void finish() {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isError() {
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
