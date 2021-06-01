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
package io.jboot.test.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 参考：https://stackoverflow.com/questions/30484388/inputstream-to-servletinputstream
 */
public class MockServletInputStream extends ServletInputStream {

    final byte[] myBytes;

    private int lastIndexRetrieved = -1;
    private ReadListener readListener = null;
    private int readLimit = -1;
    private int markedPosition = -1;

    public MockServletInputStream(byte[] myBytes) {
        this.myBytes = myBytes;
    }

    public MockServletInputStream(String content) {
        myBytes = content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean isFinished() {
        return (lastIndexRetrieved == myBytes.length - 1);
    }

    @Override
    public boolean isReady() {
        return isFinished();
    }

    @Override
    public int available() throws IOException {
        return (myBytes.length - lastIndexRetrieved - 1);
    }

    @Override
    public void close() throws IOException {
        lastIndexRetrieved = myBytes.length - 1;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (!isFinished()) {
            try {
                readListener.onDataAvailable();
            } catch (IOException e) {
                readListener.onError(e);
            }
        } else {
            try {
                readListener.onAllDataRead();
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }


    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readLimit) {
        this.readLimit = readLimit;
        this.markedPosition = lastIndexRetrieved;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (markedPosition == -1) {
            throw new IOException("No mark found");
        } else {
            lastIndexRetrieved = markedPosition;
            readLimit = -1;
        }
    }

    // Replacement of earlier read method to cope with readLimit
    @Override
    public int read() throws IOException {
        int i;
        if (!isFinished()) {
            i = myBytes[lastIndexRetrieved + 1];
            lastIndexRetrieved++;
            if (isFinished() && (readListener != null)) {
                try {
                    readListener.onAllDataRead();
                } catch (IOException ex) {
                    readListener.onError(ex);
                    throw ex;
                }
                readLimit = -1;
            }
            if (readLimit != -1) {
                if ((lastIndexRetrieved - markedPosition) > readLimit) {
                    // This part is actually not necessary in our implementation
                    // as we are not storing any data. However we need to respect
                    // the contract.
                    markedPosition = -1;
                    readLimit = -1;
                }
            }
            return i;
        } else {
            return -1;
        }
    }
}
