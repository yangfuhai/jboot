/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.render.FileRender;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
    public boolean deleteFile(String relativePath) {
        return getFile(relativePath).delete();
    }


    public File newRandomFile(String suffix) {
        String rootPath = getRootPath();

        StringBuilder newFileName = new StringBuilder(rootPath)
                .append(File.separator).append(targetPrefix)
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


    @Override
    public boolean isRemoteContainer() {
        return false;
    }


    @Override
    public boolean matchFile(String target, HttpServletRequest request) {
        return !isRunInFatjar() //非 fatjar 模式下，让容器（tomcat 或者 undertow）去渲染
                && target.startsWith(buildMatchTarget())
                && target.lastIndexOf('.') != -1;
    }

    private String matchTarget = null;

    private String buildMatchTarget() {
        if (matchTarget == null) {
            synchronized (this) {
                if (matchTarget == null) {
                    matchTarget = "/" + getTargetPrefix() + "/";
                }
            }
        }
        return matchTarget;
    }


    private Boolean runInFatjar;

    protected boolean isRunInFatjar() {
        if (runInFatjar == null) {
            runInFatjar = LocalAttachmentContainer.class.getResource("/") == null;
        }
        return runInFatjar;
    }


    @Override
    public boolean renderFile(String target, HttpServletRequest request, HttpServletResponse response) {
        new FileRender(getFile(target)).setContext(request, response).render();
        return true;
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
        this.matchTarget = null;
    }
}
