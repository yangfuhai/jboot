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
package io.jboot.web.attachment;

import com.jfinal.kit.PathKit;
import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;

import java.io.File;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@ConfigModel(prefix = "jboot.attachment")
public class LocalAttachmentContainerConfig {

    private String rootPath = PathKit.getWebRootPath();
    private String targetPrefix = "/attachment";

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

    public static LocalAttachmentContainerConfig getInstance() {
        return Jboot.config(LocalAttachmentContainerConfig.class);
    }

    public String buildUploadAbsolutePath() {
        return new File(rootPath, targetPrefix).getAbsolutePath();
    }
}
