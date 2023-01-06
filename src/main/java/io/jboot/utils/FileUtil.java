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
package io.jboot.utils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtil {

    /**
     * 获取文件后缀
     *
     * @param fileName eg: jboot.jpg
     * @return suffix eg: .jpg
     */
    public static String getSuffix(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return null;
    }


    public static String removePrefix(String src, String... prefixes) {
        if (src != null) {
            for (String prefix : prefixes) {
                if (src.startsWith(prefix)) {
                    return src.substring(prefix.length());
                }
            }
        }

        return src;
    }


    public static String removeSuffix(String src, String... suffixes) {
        if (src != null) {
            for (String suffix : suffixes) {
                if (src.endsWith(suffix)) {
                    return src.substring(0, suffix.length());
                }
            }
        }
        return src;
    }


    public static String removeRootPath(String src) {
        return removePrefix(src, PathKit.getWebRootPath());
    }


    public static String readString(File file) {
        return readString(file, JFinal.me().getConstants().getEncoding());
    }


    public static String readString(File file, String charsetName) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = fis.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            return baos.toString(charsetName);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        } finally {
            close(fis, baos);
        }
        return null;
    }


    public static void writeString(File file, String content) {
        writeString(file, content, JFinal.me().getConstants().getEncoding());
    }


    public static void writeString(File file, String content, String charsetName) {
        writeString(file, content, charsetName, false);
    }

    public static void writeString(File file, String content, String charsetName, boolean append) {
        FileOutputStream fos = null;
        try {
            ensuresParentExists(file);
            fos = new FileOutputStream(file, append);
            fos.write(content.getBytes(charsetName));
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        } finally {
            close(fos);
        }
    }

    public static void ensuresParentExists(File currentFile) throws IOException {
        if (!currentFile.getParentFile().exists()
                && !currentFile.getParentFile().mkdirs()) {
            throw new IOException("Can not mkdirs for file: " + currentFile.getParentFile());
        }
    }


    /**
     * 获取文件的 md5
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        return getFileMD5(file, false);
    }


    /**
     * 获取文件 md5 的 base64 编码
     *
     * @param file
     * @return
     */
    public static String getFileMd5Base64(File file) {
        return getFileMD5(file, true);
    }


    private static String getFileMD5(File file, boolean withBase64) {
        try (FileInputStream fiStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fiStream.read(buffer)) != -1) {
                digest.update(buffer, 0, length);
            }
            return withBase64 ? Base64Kit.encode(digest.digest()) : HashKit.toHex(digest.digest());
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        }
        return null;
    }


    public static void close(Closeable... closeable) {
        QuietlyUtil.closeQuietly(closeable);
    }


    public static void unzip(String zipFilePath) throws IOException {
        String targetPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
        unzip(zipFilePath, targetPath, true, StandardCharsets.UTF_8);
    }


    public static void unzip(String zipFilePath, String targetPath) throws IOException {
        unzip(zipFilePath, targetPath, true, StandardCharsets.UTF_8);
    }


    public static void unzip(String zipFilePath, String targetPath, boolean safeUnzip, Charset charset) throws IOException {
        targetPath = getCanonicalPath(new File(targetPath));
        ZipFile zipFile = new ZipFile(zipFilePath, charset);
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            while (entryEnum.hasMoreElements()) {
                OutputStream os = null;
                InputStream is = null;
                try {
                    ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (!zipEntry.isDirectory()) {
                        if (safeUnzip && isNotSafeFile(zipEntry.getName())) {
                            //Unsafe
                            continue;
                        }

                        File targetFile = new File(targetPath, zipEntry.getName());

                        ensuresParentExists(targetFile);

                        if (!targetFile.toPath().normalize().startsWith(targetPath)) {
                            throw new IOException("Bad zip entry");
                        }

                        os = new BufferedOutputStream(new FileOutputStream(targetFile));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 4096)) > 0) {
                            os.write(buffer, 0, readLen);
                        }
                    }
                } finally {
                    close(is, os);
                }
            }
        } finally {
            close(zipFile);
        }
    }


    private static boolean isNotSafeFile(String name) {
        name = name.toLowerCase();
        return name.endsWith(".jsp") || name.endsWith(".jspx");
    }


    public static boolean isAbsolutePath(String path) {
        return StrUtil.isNotBlank(path) && (path.startsWith("/") || path.indexOf(":") > 0);
    }


    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(File file) {
        if (file == null) {
            return;
        }

        if (!file.delete()) {
            LogKit.error("File {} can not deleted!", getCanonicalPath(file));
        }
    }

    public static void delete(UploadFile file) {
        if (file == null) {
            return;
        }

        delete(file.getFile());
    }


    public static void delete(List<UploadFile> files) {
        if (files == null) {
            return;
        }

        files.forEach(FileUtil::delete);
    }

}
