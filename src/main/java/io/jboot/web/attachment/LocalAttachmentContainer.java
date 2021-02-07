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
package io.jboot.web.attachment;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.LogKit;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class LocalAttachmentContainer implements AttachmentContainer {

    private String rootPath;
    private String targetPrefix;

    public LocalAttachmentContainer() {
        LocalAttachmentContainerConfig config = LocalAttachmentContainerConfig.getInstance();
        this.rootPath = config.getRootPath();
        this.targetPrefix = config.getTargetPrefix();
    }

    /**
     * @param rootPath
     * @param targetPrefix 不能以 / 开头
     */
    public LocalAttachmentContainer(String rootPath, String targetPrefix) {
        this.rootPath = rootPath;
        this.targetPrefix = targetPrefix;
    }


    @Override
    public String saveFile(File file) {
        File newfile = newRandomFile(FileUtil.getSuffix(file.getName()));

        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }

        try {
            org.apache.commons.io.FileUtils.moveFile(file, newfile);
            newfile.setReadable(true, false);
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }

        String attachmentRoot = getRootPath();
        return FileUtil.removePrefix(newfile.getAbsolutePath(), attachmentRoot);
    }


    @Override
    public String saveFile(File file, String toRelativePath) {
        File toFile = new File(getRootPath(), toRelativePath);
        try {

            //相同的文件，不需要做任何处理
            if (toFile.equals(file)){
                return toRelativePath;
            }

            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }

            org.apache.commons.io.FileUtils.moveFile(file, toFile);
            toFile.setReadable(true, false);

        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }

        return toRelativePath;


    }

    @Override
    public String saveFile(InputStream inputStream, String toRelativePath) {

        File toFile = new File(getRootPath(), toRelativePath);

        if (toFile.exists()){
            toFile.delete();
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        FileOutputStream fOutStream = null;
        try {
            fOutStream = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                fOutStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        } finally {
            FileUtil.close(fOutStream, inputStream);
        }
        return toRelativePath;
    }


    @Override
    public boolean deleteFile(String relativePath) {
        File file = getFile(relativePath);
        return file != null && file.delete();
    }


    public File newRandomFile(String suffix) {
        String rootPath = getRootPath();

        StringBuilder newFileName = new StringBuilder(rootPath).append(targetPrefix)
                .append(File.separator).append(DateKit.toStr(new Date(), "yyyyMMdd"))
                .append(File.separator).append(StrUtil.uuid())
                .append(suffix);

        return new File(newFileName.toString());
    }


    @Override
    public File getFile(String relativePath) {
        return new File(getRootPath(), relativePath);
    }


    @Override
    public String getRelativePath(File file) {
        String rootPath = getRootPath();
        String filePath = file.getAbsolutePath();
        return filePath.startsWith(rootPath)
                ? filePath.substring(rootPath.length())
                : filePath;
    }


    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getTargetPrefix() {
        return targetPrefix;
    }

    public void setTargetPrefix(String targetPrefix) {
        this.targetPrefix = targetPrefix;
    }
}
