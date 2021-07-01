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
import io.jboot.utils.StrUtil;

import java.io.File;

public class ApiDocConfig {

    private String basePath = "";
    private String packagePrefix;


    private boolean allInOneEnable = false;
    private String allInOneTitle = "Api Document";
    private String allInOneNotes;
    private String allInOneFilePath = "apidoc";

    private String mockJsonPath = "api-mock.json";
    private String modelJsonPath = "api-mock.json";


    public String getBasePath() {
        return basePath;
    }

    public String getBasePathAbsolute() {
        if (isAbsolutePath(basePath)) {
            return basePath;
        }
        return new File(PathKit.getRootClassPath(), "../../" + basePath).getAbsolutePath();
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


    public boolean isAllInOneEnable() {
        return allInOneEnable;
    }

    public void setAllInOneEnable(boolean allInOneEnable) {
        this.allInOneEnable = allInOneEnable;
    }

    public String getAllInOneTitle() {
        return allInOneTitle;
    }

    public void setAllInOneTitle(String allInOneTitle) {
        this.allInOneTitle = allInOneTitle;
    }

    public String getAllInOneNotes() {
        return allInOneNotes;
    }

    public void setAllInOneNotes(String allInOneNotes) {
        this.allInOneNotes = allInOneNotes;
    }

    public String getAllInOneFilePath() {
        return allInOneFilePath;
    }

    public void setAllInOneFilePath(String allInOneFilePath) {
        this.allInOneFilePath = allInOneFilePath;
    }

    public String getMockJsonPath() {
        return mockJsonPath;
    }

    public void setMockJsonPath(String mockJsonPath) {
        this.mockJsonPath = mockJsonPath;
    }

    public String getMockJsonPathAbsolute() {
        if (isAbsolutePath(mockJsonPath)) {
            return mockJsonPath;
        }
        return new File(PathKit.getRootClassPath(), mockJsonPath).getAbsolutePath();
    }

    public String getModelJsonPath() {
        return modelJsonPath;
    }

    public void setModelJsonPath(String modelJsonPath) {
        this.modelJsonPath = modelJsonPath;
    }

    public String getModelJsonPathAbsolute() {
        if (isAbsolutePath(modelJsonPath)) {
            return modelJsonPath;
        }
        return new File(PathKit.getRootClassPath(), modelJsonPath).getAbsolutePath();
    }

    /**
     * 判断是否是绝对路径
     *
     * @param path
     * @return true：绝对路径
     */
    private static boolean isAbsolutePath(String path) {
        return StrUtil.isNotBlank(path) && (path.startsWith("/") || path.indexOf(":") > 0);
    }

}
