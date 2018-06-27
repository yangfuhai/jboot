/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.component.shiro.processer;

import java.io.Serializable;

/**
 * 认证处理器 执行后的认证结果。
 */
public class AuthorizeResult implements Serializable {

    public static final int CODE_SUCCESS = 0;

    /**
     * 未进行身份认证
     */
    public static final int ERROR_CODE_UNAUTHENTICATED = 1;


    /**
     * 没有权限访问
     */
    public static final int ERROR_CODE_UNAUTHORIZATION = 2;



    private int errorCode = CODE_SUCCESS;


    public int getErrorCode() {
        return errorCode;
    }

    public AuthorizeResult setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public boolean isOk() {
        return errorCode == CODE_SUCCESS;
    }


    public static AuthorizeResult ok() {
        return new AuthorizeResult();
    }


    public static AuthorizeResult fail(int errorCode) {
        return new AuthorizeResult().setErrorCode(errorCode);
    }


}
