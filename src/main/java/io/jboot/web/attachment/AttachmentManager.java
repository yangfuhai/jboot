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

import com.jfinal.log.Log;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class AttachmentManager {

    private static final Log LOG = Log.getLog(AttachmentManager.class);


    private static final AttachmentManager ME = new AttachmentManager();

    public static AttachmentManager me() {
        return ME;
    }

    private AttachmentManager() {
    }

    /**
     * 默认的 附件容器
     */
    private AttachmentContainer defaultContainer = new LocalAttachmentContainer();

    /**
     * 其他附件容器
     */
    private List<AttachmentContainer> containers = new CopyOnWriteArrayList<>();


    public AttachmentContainer getDefaultContainer() {
        return defaultContainer;
    }

    public void setDefaultContainer(AttachmentContainer defaultContainer) {
        this.defaultContainer = defaultContainer;
    }

    public void addContainer(AttachmentContainer container) {
        containers.add(container);
    }

    public void setContainers(List<AttachmentContainer> containers) {
        this.containers = containers;
    }


    public List<AttachmentContainer> getContainers() {
        return containers;
    }


    public AttachmentContainer matchContainer(String target, HttpServletRequest request) {

        if (defaultContainer.matchFile(target, request)) {
            return defaultContainer;
        }

        for (AttachmentContainer container : containers) {
            if (container.matchFile(target, request)) {
                return container;
            }
        }

        return null;
    }


    /**
     * 保存文件
     *
     * @param file
     * @return 返回文件的相对路径
     */
    public String saveFile(File file) {
        //优先从 默认的 container 去保存文件
        String relativePath = defaultContainer.saveFile(file);
        File defaultContainerFile = defaultContainer.getFile(relativePath);

        for (AttachmentContainer container : containers) {
            try {
                if (container != defaultContainer) {
                    container.saveFile(defaultContainerFile);
                }
            } catch (Exception ex) {
                LOG.error("get file error in container :" + container, ex);
            }
        }
        return relativePath.replace("\\", "/");
    }


    /**
     * 删除文件
     *
     * @param relativePath
     * @return
     */
    public boolean deleteFile(String relativePath) {
        for (AttachmentContainer container : containers) {
            try {
                container.deleteFile(relativePath);
            } catch (Exception ex) {
                LOG.error("delete file error in container :" + container, ex);
            }
        }
        return defaultContainer.deleteFile(relativePath);
    }

    /**
     * 通过相对路径获取文件
     *
     * @param relativePath
     * @return
     */
    public File getFile(String relativePath) {

        //优先从 默认的 container 去获取
        File file = defaultContainer.getFile(relativePath);
        if (file != null && file.exists()) {
            return file;
        }

        for (AttachmentContainer container : containers) {
            try {
                file = container.getFile(relativePath);
                if (file != null && file.exists()) {
                    return file;
                }
            } catch (Exception ex) {
                LOG.error("get file error in container :" + container, ex);
            }
        }
        return null;
    }

    /**
     * 通过一个文件，获取其相对路径
     *
     * @param file
     * @return
     */
    public String getRelativePath(File file) {
        String relativePath = defaultContainer.getRelativePath(file);
        return relativePath != null ? relativePath.replace("\\", "/") : null;
    }
}
