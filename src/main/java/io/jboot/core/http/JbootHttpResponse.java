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
package io.jboot.core.http;

import com.jfinal.log.Log;

import java.io.*;
import java.util.List;
import java.util.Map;

public class JbootHttpResponse {
    private static final Log log = Log.getLog(JbootHttpResponse.class);

    private OutputStream outputStream;
    private File file;
    private Throwable error;
    private Map<String, List<String>> headers;
    private int responseCode;
    private String contentType;

    public JbootHttpResponse() {
        this.outputStream = new ByteArrayOutputStream();
    }

    public JbootHttpResponse(File file) {
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
    public String getContent() {
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
            setError(throwable);
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

    public boolean isNotError() {
        return !isError();
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
