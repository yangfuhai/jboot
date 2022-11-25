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

import com.jfinal.upload.MultipartRequest;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import io.jboot.utils.ReflectUtil;

import java.io.File;

public class SecurityFileNamePolicy extends DefaultFileRenamePolicy {

    @Override
    public File rename(File f) {
        String name = f.getName();

        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf > 0) {
            String suffix = name.substring(lastIndexOf).toLowerCase().trim();
            if (".jsp".equals(suffix) || ".jspx".equals(suffix)) {
                File safeFile = new File(f.getParentFile(), name + "_unsafe");
                return super.rename(safeFile);
            }
        }

        return super.rename(f);
    }


    public static void init() {
        ReflectUtil.setStaticFieldValue(MultipartRequest.class, "fileRenamePolicy", new SecurityFileNamePolicy());
    }
}
