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
package io.jboot.apidoc;

import com.jfinal.kit.PathKit;

import java.io.File;

public class ApiDocConfig {

    private String basePath;
    private String packagePrefix;

    public String getBasePath() {
        return basePath;
    }

    public String getBasePathAbsolute() {
        if (isAbsolutePath(basePath)){
            return basePath;
        }
        return new File(PathKit.getRootClassPath(),"../../" + basePath).getAbsolutePath();
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }


    /**
     * 判断是否是绝对路径
     *
     * @param path
     * @return true：绝对路径
     */
    private static boolean isAbsolutePath(String path) {
        return path.startsWith("/") || path.indexOf(":") > 0;
    }

}
