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
package io.jboot.app.config;

import java.io.*;
import java.util.Properties;


class JbootProp {
    protected Properties properties = null;
    private static final String DEFAULT_ENCODING = "UTF-8";

    public JbootProp(String fileName) {
        this(fileName, DEFAULT_ENCODING);
    }

    public JbootProp(String fileName, String encoding) {
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = ConfigUtil.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, encoding));
            }
        } catch (Exception e) {
            System.err.println("Warning: Can not load properties file in classpath, file name: " + fileName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }


    public JbootProp(File file) {
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
            }
        } catch (Exception e) {
            System.err.println("Warning: Can not load properties file: " + file);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
