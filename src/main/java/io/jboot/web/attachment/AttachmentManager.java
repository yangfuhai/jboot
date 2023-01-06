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

import com.jfinal.log.Log;
import com.jfinal.render.IRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class AttachmentManager {

    private static final Log LOG = Log.getLog(AttachmentManager.class);

    private static Map<String, AttachmentManager> managers = new HashMap<>();

    public static AttachmentManager me() {
        return use("default");
    }

    public static AttachmentManager use(String name) {
        AttachmentManager manager = managers.get(name);
        if (manager == null) {
            synchronized (AttachmentManager.class) {
                manager = managers.get(name);
                if (manager == null) {
                    manager = new AttachmentManager(name);
                    managers.put(name, manager);
                }
            }
        }
        return manager;
    }

    /**
     * 通过这个方式可以来更改 manager 包括默认的 manager
     *
     * @param manager
     */
    public static void setManager(AttachmentManager manager) {
        managers.put(manager.name, manager);
    }


    public AttachmentManager(String name) {
        this.name = name;
    }

    /**
     * 默认的 附件容器
     */
    protected LocalAttachmentContainer defaultContainer = new LocalAttachmentContainer();

    /**
     * 其他附件容器
     */
    protected List<AttachmentContainer> containers = new CopyOnWriteArrayList<>();

    /**
     * 自定义文件渲染器
     */
    protected IRenderFactory renderFactory = RenderManager.me().getRenderFactory();

    /**
     * manager  的名称
     */
    protected final String name;


    public String getName() {
        return name;
    }

    public IRenderFactory getRenderFactory() {
        return renderFactory;
    }

    public void setRenderFactory(IRenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }


    public LocalAttachmentContainer getDefaultContainer() {
        return defaultContainer;
    }

    public void setDefaultContainer(LocalAttachmentContainer defaultContainer) {
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
                LOG.error("Save file error in container: " + container, ex);
            }
        }
        return relativePath.replace("\\", "/");
    }


    /**
     * 保存文件
     *
     * @param file
     * @return 返回文件的相对路径
     */
    public String saveFile(File file, String toRelativePath) {
        //优先从 默认的 container 去保存文件
        String relativePath = defaultContainer.saveFile(file, toRelativePath);
        File defaultContainerFile = defaultContainer.getFile(relativePath);

        for (AttachmentContainer container : containers) {
            try {
                if (container != defaultContainer) {
                    container.saveFile(defaultContainerFile, toRelativePath);
                }
            } catch (Exception ex) {
                LOG.error("Save file error in container: " + container, ex);
            }
        }
        return relativePath.replace("\\", "/");
    }

    /**
     * 保存文件
     *
     * @param inputStream
     * @return
     */
    public String saveFile(InputStream inputStream, String toRelativePath) {
        //优先从 默认的 container 去保存文件
        String relativePath = defaultContainer.saveFile(inputStream, toRelativePath);
        File defaultContainerFile = defaultContainer.getFile(relativePath);

        for (AttachmentContainer container : containers) {
            try {
                if (container != defaultContainer) {
                    container.saveFile(defaultContainerFile, toRelativePath);
                }
            } catch (Exception ex) {
                LOG.error("Save file error in container: " + container, ex);
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
                LOG.error("Delete file error in container: " + container, ex);
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
        return getFile(relativePath, true);
    }


    /**
     * 通过相对路径获取文件
     *
     * @param relativePath
     * @param localFirst
     * @return
     */
    public File getFile(String relativePath, boolean localFirst) {
        //优先从 默认的 container 去获取
        if (localFirst) {
            File localFile = defaultContainer.getFile(relativePath);
            if (localFile.exists()) {
                return localFile;
            }
        }

        for (AttachmentContainer container : containers) {
            if (container != defaultContainer) {
                try {
                    File file = container.getFile(relativePath);
                    if (file != null && file.exists()) {
                        return file;
                    }
                } catch (Exception ex) {
                    LOG.error("Get file error in container: " + container, ex);
                }
            }
        }

        //文件不存在，也返回该文件
        return defaultContainer.getFile(relativePath);
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


    /**
     * 创建一个新的文件
     * 使用创建一般是创建一个空的文件，然后由外部逻辑进行写入
     *
     * @param suffix
     * @return
     */
    public File createNewFile(String suffix) {
        return getDefaultContainer().creatNewFile(suffix);
    }


    /**
     * 渲染文件到浏览器
     *
     * @param target
     * @param request
     * @param response
     * @return true 渲染成功，false 不进行渲染
     */
    public boolean renderFile(String target, HttpServletRequest request, HttpServletResponse response) {
        if (target.startsWith(defaultContainer.getTargetPrefix())
                && target.lastIndexOf('.') != -1) {
            Render render;
            if (target.contains("..")) {
                render = renderFactory.getErrorRender(404);
            } else {
                File file = getFile(target);
                render = getFileRender(file);
            }
            render.setContext(request, response).render();
            return true;
        } else {
            return false;
        }
    }


    protected Render getFileRender(File file) {
        return file == null || !file.isFile()
                ? renderFactory.getErrorRender(404)
                : renderFactory.getFileRender(file);
    }


}
