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

import java.io.Serializable;

public class ClassType implements Serializable {

    private Class<?> mainClass; //类
    private ClassType[] genericTypes;//泛型

    public ClassType() {
    }

    public ClassType(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public ClassType(Class<?> mainClass, ClassType[] genericTypes) {
        this.mainClass = mainClass;
        this.genericTypes = genericTypes;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public ClassType[] getGenericTypes() {
        return genericTypes;
    }

    public void setGenericTypes(ClassType[] genericTypes) {
        this.genericTypes = genericTypes;
    }

    public String getDataType() {
        return mainClass.getSimpleName();
    }

    public boolean isGeneric() {
        return genericTypes != null && genericTypes.length > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mainClass.getSimpleName());
        if (isGeneric()) {
            sb.append("<");
            for (int i = 0; i < genericTypes.length; i++) {
                sb.append(genericTypes[i].toString());
                if (i != genericTypes.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }
}
