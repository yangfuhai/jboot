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
package io.jboot.app.config;

import com.jfinal.kit.LogKit;

import java.io.*;
import java.net.URL;
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
            inputStream = getResourceAsStreamByCurrentThread(fileName);

            if (inputStream == null) {
                inputStream = getResourceAsStreamByClassloader(fileName);
            }

            // 当系统未编译的时候，开发环境下的 resources 目录下的 jboot.properties 文件不会自动被 copy 到 target/classes 目录下
            // 此时，需要主动去探测 resources 目录的文件
            if (inputStream == null) {
                URL resourceURL = JbootProp.class.getResource("/");
                if (resourceURL != null) {
                    String classPath = resourceURL.toURI().getPath();

                    if (removeSlashEnd(classPath).endsWith("classes")) {

                        //from classes path
                        File propFile = new File(classPath, fileName);
                        if (propFile.exists() && propFile.isFile()) {
                            inputStream = new FileInputStream(propFile);
                        }

                        //from resources path
                        else {
                            File resourcesDir = new File(classPath, "../../src/main/resources");
                            propFile = new File(resourcesDir, fileName);
                            if (propFile.exists() && propFile.isFile()) {
                                inputStream = new FileInputStream(propFile);
                            }
                        }
                    }
                }
            }

            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, encoding));
            } else if (!fileName.contains("-")) {
                System.err.println("Warning: Can not load properties file in classpath, file name: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("Warning: Can not load properties file in classpath, file name: " + fileName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogKit.logNothing(e);
                }
            }
        }
    }

    private InputStream getResourceAsStreamByCurrentThread(String fileName) {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret.getResourceAsStream(fileName) : null;
    }


    private InputStream getResourceAsStreamByClassloader(String fileName) {
        ClassLoader ret = JbootProp.class.getClassLoader();
        return ret != null ? ret.getResourceAsStream(fileName) : null;
    }


    private static String removeSlashEnd(String path) {
        if (path != null && (path.endsWith("/") || path.endsWith("\\"))) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }


    public JbootProp(File file) {
        properties = new Properties();
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
        } catch (Exception e) {
            System.err.println("Warning: Can not load properties file: " + file);
        }
    }


    public Properties getProperties() {
        return properties;
    }
}
