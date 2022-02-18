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
package io.jboot.core.log;

import com.jfinal.log.Log;
import io.jboot.exception.JbootExceptionHolder;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author michael
 */
public class Slf4jLogger extends Log {

    private LocationAwareLogger log;

    private static final Object[] NULL_ARGS = new Object[0];
    private static final String callerFQCN = Slf4jLogger.class.getName();

    public Slf4jLogger(LocationAwareLogger log) {
        this.log = log;
    }

    @Override
    public void trace(String message) {
        log.log(null, callerFQCN, LocationAwareLogger.TRACE_INT, message, NULL_ARGS, null);
    }

    @Override
    public void trace(String message, Throwable t) {
        log.log(null, callerFQCN, LocationAwareLogger.TRACE_INT, message, NULL_ARGS, t);
    }

    @Override
    public void debug(String message) {
        log.log(null, callerFQCN, LocationAwareLogger.DEBUG_INT, message, NULL_ARGS, null);
    }

    @Override
    public void debug(String message, Throwable t) {
        log.log(null, callerFQCN, LocationAwareLogger.DEBUG_INT, message, NULL_ARGS, t);
    }

    @Override
    public void info(String message) {
        log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, message, NULL_ARGS, null);
    }

    @Override
    public void info(String message, Throwable t) {
        log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, message, NULL_ARGS, t);
    }

    @Override
    public void warn(String message) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, message, NULL_ARGS, null);
    }

    @Override
    public void warn(String message, Throwable t) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, message, NULL_ARGS, t);
    }

    @Override
    public void error(String message) {
        JbootExceptionHolder.hold(message,null);
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, message, NULL_ARGS, null);
    }

    @Override
    public void error(String message, Throwable t) {
        JbootExceptionHolder.hold(message, t);
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, message, NULL_ARGS, t);
    }

    @Override
    public void fatal(String message) {
        // throw new UnsupportedOperationException("slf4j logger does not support fatal level");
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, message, NULL_ARGS, null);
    }

    @Override
    public void fatal(String message, Throwable t) {
        JbootExceptionHolder.hold(message, t);
        // throw new UnsupportedOperationException("slf4j logger does not support fatal level");
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, message, NULL_ARGS, t);
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
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
        // throw new UnsupportedOperationException("slf4j logger does not support fatal level");
        return log.isErrorEnabled();
    }

    // -------------------------------------------------------

    @Override
    public void trace(String format, Object... args) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            log.log(null, callerFQCN, LocationAwareLogger.TRACE_INT, ft.getMessage(), NULL_ARGS, ft.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            log.log(null, callerFQCN, LocationAwareLogger.DEBUG_INT, ft.getMessage(), NULL_ARGS, ft.getThrowable());
        }
    }

    @Override
    public void info(String format, Object... args) {
        if (isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, ft.getMessage(), NULL_ARGS, ft.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object... args) {
        if (isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, ft.getMessage(), NULL_ARGS, ft.getThrowable());
        }
    }

    @Override
    public void error(String format, Object... args) {
        if (isErrorEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
            JbootExceptionHolder.hold(ft.getMessage(), ft.getThrowable());
            log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, ft.getMessage(), NULL_ARGS, ft.getThrowable());
        }
    }

    @Override
    public void fatal(String format, Object... args) {
        // throw new UnsupportedOperationException("slf4j logger does not support fatal level");
        error(format, args);
    }
}
