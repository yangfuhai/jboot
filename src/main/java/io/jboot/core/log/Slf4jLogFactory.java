/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author michael
 */
public class Slf4jLogFactory extends com.jfinal.log.Slf4jLogFactory {

    private boolean useJdkLogger;

    public Slf4jLogFactory() {
        boolean hasStaticLoggerBinder = false;
        try {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            hasStaticLoggerBinder = true;
        } catch (ClassNotFoundException e) {}

        if (!hasStaticLoggerBinder) {
            useJdkLogger = true;
        } else {
            this.useJdkLogger = LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory;
        }
    }

    @Override
    public Log getLog(Class<?> clazz) {
        if (useJdkLogger) {
            return new JdkLogger(clazz);
        }

        Logger log = LoggerFactory.getLogger(clazz);
        return log instanceof LocationAwareLogger ? new Slf4jLogger((LocationAwareLogger) log) : new Slf4jSimpleLogger(log);
    }

    @Override
    public Log getLog(String name) {
        if (useJdkLogger) {
            return new JdkLogger(name);
        }

        Logger log = LoggerFactory.getLogger(name);
        return log instanceof LocationAwareLogger ? new Slf4jLogger((LocationAwareLogger) log) : new Slf4jSimpleLogger(log);
    }


}
