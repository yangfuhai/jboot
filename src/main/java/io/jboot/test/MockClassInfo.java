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
package io.jboot.test;

class MockClassInfo<T> {

    private Class<T> mockClass;
    private Class<? super T> targetClass;

    public MockClassInfo(Class<T> mockClass, Class<? super T> targetClass) {
        this.mockClass = mockClass;
        this.targetClass = targetClass;
    }

    public Class<T> getMockClass() {
        return mockClass;
    }

    public void setMockClass(Class<T> mockClass) {
        this.mockClass = mockClass;
    }

    public Class<? super T> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<? super T> targetClass) {
        this.targetClass = targetClass;
    }
}
