package io.jboot.app.config;

import java.io.*;
import java.util.Properties;


class Prop {

    protected Properties properties = null;
    private static final String DEFAULT_ENCODING = "UTF-8";

    public Prop(String fileName) {
        this(fileName, DEFAULT_ENCODING);
    }

    public Prop(String fileName, String encoding) {
        InputStream inputStream = null;
        try {
            inputStream = Utils.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("properties file not found in classpath,  fileName : " + fileName);
            }
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("error loading properties file.", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
