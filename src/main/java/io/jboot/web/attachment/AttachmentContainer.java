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

import java.io.File;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public interface AttachmentContainer {

    /**
     * 保存文件
     * @param file
     * @return 返回文件的相对路径
     */
    public String saveFile(File file);


    /**
     * 删除文件
     * @param relativePath
     * @return
     */
    public boolean deleteFile(String relativePath);


    /**
     * 通过相对路径获取文件
     * @param relativePath
     * @return
     */
    public File getFile(String relativePath);


    /**
     * 通过一个文件，获取其相对路径
     * @param file
     * @return
     */
    public String getRelativePath(File file);



}
