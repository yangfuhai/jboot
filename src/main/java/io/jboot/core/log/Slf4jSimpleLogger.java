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
package io.jboot.core.log;

import com.jfinal.log.Log;
import io.jboot.exception.JbootExceptionHolder;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/12
 */
public class Slf4jSimpleLogger extends Log {

    private org.slf4j.Logger log;

    public Slf4jSimpleLogger(org.slf4j.Logger log) {
        this.log = log;
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    @Override
    public void debug(String message, Throwable t) {
        log.debug(message, t);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Throwable t) {
        log.info(message, t);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable t) {
        JbootExceptionHolder.hold(message, t);
        log.error(message, t);
    }

    @Override
    public void fatal(String message) {
        log.error(message);
    }

    @Override
    public void fatal(String message, Throwable t) {
        JbootExceptionHolder.hold(message, t);
        log.error(message, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return log.isErrorEnabled();
    }

    // -------------------------------------------------------

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        log.trace(message);
    }

    @Override
    public void trace(String message, Throwable t) {
        log.trace(message, t);
    }

    // -------------------------------------------------------

    @Override
    public void trace(String format, Object... args) {
        if (isTraceEnabled()) {
            log.trace(format, args);
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (isDebugEnabled()) {
            log.debug(format, args);
        }
    }

    @Override
    public void info(String format, Object... args) {
        if (isInfoEnabled()) {
            log.info(format, args);
        }
    }

    @Override
    public void warn(String format, Object... args) {
        if (isWarnEnabled()) {
            log.warn(format, args);
        }
    }

    @Override
    public void error(String format, Object... args) {
        if (isErrorEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            JbootExceptionHolder.hold(ft.getMessage(), ft.getThrowable());
            log.error(format, args);
        }
    }

    @Override
    public void fatal(String format, Object... args) {
        if (isFatalEnabled()) {
            log.error(format, args);
        }
    }
}