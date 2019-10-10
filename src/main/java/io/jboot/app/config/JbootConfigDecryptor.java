/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app.config;

/**
 * Jboot Config 的内容解密器，加密方式由可客户自己编写的加密算法来加密
 * <p>
 * 此时，只需要给 JbootConfigManager 配置上 Decryptor 进行解密即可
 */
public interface JbootConfigDecryptor {

    public String decrypt(String key, String encryptContent);
}
