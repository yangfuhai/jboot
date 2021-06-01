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
package io.jboot.test.junit5;

import com.jfinal.aop.Aop;
import io.jboot.test.MockApp;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JbootExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        MockApp.getInstance().start();
    }


    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        MockApp.getInstance().stop();
    }


    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if (extensionContext.getTestInstance().isPresent()) {
            Aop.inject(extensionContext.getTestInstance().get());
        }
    }


}
