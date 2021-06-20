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
package io.jboot.test.web;

import com.jfinal.core.Action;
import com.jfinal.core.ActionException;
import io.jboot.utils.StrUtil;
import io.jboot.web.handler.JbootActionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockActionHandler extends JbootActionHandler {

    @Override
    protected void handleActionException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ActionException e) {
        int errorCode = e.getErrorCode();
        String msg = null;
        if (errorCode == 404) {
            msg = "404 Not Found.";
        } else if (errorCode == 400) {
            msg = "400 Bad Request. ";
        } else if (errorCode == 401) {
            msg = "401 Unauthorized. ";
        } else if (errorCode == 403) {
            msg = "403 Forbidden. ";
        }
        throw new AssertionError(msg, e);
    }


    @Override
    protected void handleException(String target, HttpServletRequest request, HttpServletResponse response, Action action, Exception e) {
        throw new AssertionError("500 error: " + (StrUtil.isNotBlank(e.getMessage()) ? e.getMessage() : e.getClass().getSimpleName()), e);
    }
}
