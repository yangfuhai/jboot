/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app;


import java.io.*;

class Banner {

    public static String getText(String file) {
        File bannerFile = new File(getRootClassPath(), file);
        if (bannerFile.exists() && bannerFile.canRead()) {
            String bannerFileText = readString(bannerFile);
            if (bannerFileText != null && bannerFileText.trim().length() != 0) {
                return bannerFileText;
            }
        }

        return "  ____  ____    ___    ___   ______ \n" +
                " |    ||    \\  /   \\  /   \\ |      |\n" +
                " |__  ||  o  )|     ||     ||      |\n" +
                " __|  ||     ||  O  ||  O  ||_|  |_|\n" +
                "/  |  ||  O  ||     ||     |  |  |  \n" +
                "\\  `  ||     ||     ||     |  |  |  \n" +
                " \\____||_____| \\___/  \\___/   |__|  \n" +
                "                                    ";

    }


    private static String getRootClassPath() {
        try {
            String path = getClassLoader().getResource("").toURI().getPath();
            return new File(path).getAbsolutePath();
        } catch (Exception e) {
            try {
                String path = Banner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                path = java.net.URLDecoder.decode(path, "UTF-8");
                if (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }
                /**
                 * Fix path带有文件名
                 */
                if (path.endsWith(".jar")) {
                    path = path.substring(0, path.lastIndexOf("/") + 1);
                }
                return path;
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException(e1);
            }
        }
    }


    private static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : Banner.class.getClassLoader();
    }


    private static String readString(File file) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = fis.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception e) {
        } finally {
            close(fis, baos);
        }
        return null;
    }

    private static void close(InputStream is, OutputStream os) {
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
            }
        if (os != null)
            try {
                os.close();
            } catch (IOException e) {
            }
    }

}
