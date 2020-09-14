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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class AttachmentManager {

    private static final Log LOG = Log.getLog(AttachmentManager.class);
    private static final String DEFAULT_KEY = "default";


    private static final AttachmentManager ME = new AttachmentManager();

    public static AttachmentManager me() {
        return ME;
    }

    private AttachmentManager() {
        containerMap.put(DEFAULT_KEY, new LocalAttachmentContainer());
    }


    private Map<String, AttachmentContainer> containerMap = new ConcurrentHashMap<>();


    public AttachmentContainer matchContainer(String target, HttpServletRequest request) {
        if (containerMap.size() == 0) {
            return null;
        }

        for (AttachmentContainer container : containerMap.values()) {
            if (container.matchFile(target, request)) {
                return container;
            }
        }

        return null;
    }


    public void addContainer(String name, AttachmentContainer container) {
        containerMap.put(name, container);
    }

    public AttachmentContainer getContainer(String name) {
        return getContainer(name);
    }

    public Map<String, AttachmentContainer> getContainerMap() {
        return containerMap;
    }

    public AttachmentContainer getDefaultContainer() {
        AttachmentContainer defaultContainer = containerMap.get(DEFAULT_KEY);
        if (defaultContainer != null) {
            return defaultContainer;
        }

        if (containerMap.size() > 0) {
            return containerMap.values().stream().findFirst().get();
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
        AttachmentContainer defaultContainer = getDefaultContainer();

        //优先从 默认的 container 去保存文件
        String relativePath = defaultContainer.saveFile(file);
        File defaultContainerFile = defaultContainer.getFile(relativePath);

        for (Map.Entry<String, AttachmentContainer> entry : containerMap.entrySet()) {
            AttachmentContainer container = entry.getValue();
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
        boolean ret = false;
        for (Map.Entry<String, AttachmentContainer> entry : containerMap.entrySet()) {
            AttachmentContainer container = entry.getValue();
            try {
                boolean result = container.deleteFile(relativePath);
                if (DEFAULT_KEY.equals(entry.getKey())) {
                    ret = result;
                }
            } catch (Exception ex) {
                LOG.error("delete file error in container :" + container, ex);
            }
        }
        return ret;
    }

    /**
     * 通过相对路径获取文件
     *
     * @param relativePath
     * @return
     */
    public File getFile(String relativePath) {

        AttachmentContainer defaultContainer = getDefaultContainer();

        //优先从 默认的 container 去获取
        File file = defaultContainer.getFile(relativePath);
        if (file != null && file.exists()) {
            return file;
        }

        for (Map.Entry<String, AttachmentContainer> entry : containerMap.entrySet()) {
            AttachmentContainer container = entry.getValue();
            try {
                if (container != defaultContainer) {
                    file = container.getFile(relativePath);
                    if (file != null && file.exists()) {
                        return file;
                    }
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
        String relativePath = getDefaultContainer().getRelativePath(file);
        return relativePath != null ? relativePath.replace("\\", "/") : null;
    }
}
