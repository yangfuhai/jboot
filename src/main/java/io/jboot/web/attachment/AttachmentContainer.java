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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public interface AttachmentContainer {

    public String saveFile(File file);
    public boolean deleteFile(String relativePath);
    public File newRandomFile(String suffix);
    public File getFile(String relativePath);
    public String getRelativePath(File file);
    public boolean isSafeFile(File file);
    public boolean isRemoteContainer();

    public boolean matchFile(String target, HttpServletRequest request);
    public void renderFile(String target, HttpServletRequest request, HttpServletResponse response);
}
