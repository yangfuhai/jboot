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
package io.jboot.test;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TestConfig {

    //webRootPath 配置
    String webRootPath() default MockApp.DEFAULT_WEB_ROOT_PATH;

    //class path 配置
    String classPath() default MockApp.DEFAULT_CLASS_PATH;

    //自动 Mock Interface 接口，有些接口没有实现类的，当执行其方法时，啥都不操作，若有返回值则返回 null
    boolean autoMockInterface() default false;

    //配置 appMode
    boolean devMode() default true;

    //配置启动是否打印 class 扫描信息
    boolean printScannerInfo() default false;

    //配置启动参数
    String[] launchArgs() default "";

}