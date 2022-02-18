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
package io.jboot.apidoc;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.FileUtil;

import java.io.File;

public class ApiDocConfig {

    private String basePath = "apidoc";
    private String packagePrefix;


    private boolean allInOneEnable = false;
    private String allInOneTitle = "Api Document";
    private String allInOneNotes;
    private String allInOneFilePath = "apidoc";

    private String mockJsonPath = "api-mock.json";
    private String remarksJsonPath = "api-remarks.json";

    private Class<?> defaultContainerClass = Ret.class;


    public String getBasePath() {
        return basePath;
    }

    public String getBasePathAbsolute() {
        if (FileUtil.isAbsolutePath(basePath)) {
            return basePath;
        }
        return FileUtil.getCanonicalPath(new File(PathKit.getRootClassPath(), "../../" + basePath));
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
        if (FileUtil.isAbsolutePath(mockJsonPath)) {
            return mockJsonPath;
        }
        return new File(PathKit.getRootClassPath(), mockJsonPath).getAbsolutePath();
    }

    public String getRemarksJsonPath() {
        return remarksJsonPath;
    }

    public void setRemarksJsonPath(String remarksJsonPath) {
        this.remarksJsonPath = remarksJsonPath;
    }

    public String getRemarksJsonPathAbsolute() {
        if (FileUtil.isAbsolutePath(remarksJsonPath)) {
            return remarksJsonPath;
        }
        return new File(PathKit.getRootClassPath(), remarksJsonPath).getAbsolutePath();
    }

    public Class<?> getDefaultContainerClass() {
        return defaultContainerClass;
    }

    public void setDefaultContainerClass(Class<?> defaultContainerClass) {
        this.defaultContainerClass = defaultContainerClass;
    }
}
