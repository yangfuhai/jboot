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
package io.jboot.support.sentinel.datasource;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.jfinal.kit.LogKit;
import io.jboot.utils.FileUtil;

import java.io.File;

public class FileDataSource<T> extends AbstractDataSource<String, T> {

    private File file;

    private boolean fileExists;
    private long fileLastModified = -1;
    private long fileLength = -1;
    private boolean isClosed = false;

    public FileDataSource(File file, Converter<String, T> parser) {
        super(parser);
        this.file = file;
        this.fileExists = file.exists();

        if (this.fileExists) {
            this.fileLastModified = file.lastModified();
            this.fileLength = file.length();
        }

        updateProperties();

        new Thread(() -> {
            while (!isClosed) {
                try {
                    doReadAndUpdateProperties();
                } catch (Exception ex) {
                    LogKit.error(ex.toString(), ex);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "jboot-sentinel-file-reader").start();
    }


    private void doReadAndUpdateProperties() {
        boolean fileExists = file.exists();
        long fileLastModified = fileExists ? file.lastModified() : -1;
        long fileLength = fileExists ? file.length() : -1;

        if (this.fileExists != fileExists
                || this.fileLength != fileLength
                || this.fileLastModified != fileLastModified) {

            updateProperties();

            this.fileExists = fileExists;
            this.fileLastModified = fileLastModified;
            this.fileLength = fileLength;
        }
    }


    private void updateProperties() {
        String content = file.exists() ? FileUtil.readString(file) : "";
        getProperty().updateValue(parser.convert(content));
    }


    @Override
    public String readSource() throws Exception {
        return FileUtil.readString(file);
    }


    @Override
    public void close() throws Exception {
        isClosed = true;
    }
}
