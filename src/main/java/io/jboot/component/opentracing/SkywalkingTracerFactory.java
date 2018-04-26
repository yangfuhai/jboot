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
package io.jboot.component.opentracing;

import io.opentracing.Tracer;
import org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer;

/**
 * Skywalking 手动埋点支撑
 * https://github.com/apache/incubator-skywalking/blob/master/docs/cn/Opentracing-CN.md
 */
public class SkywalkingTracerFactory implements TracerFactory {


    private Tracer tracer;

    public SkywalkingTracerFactory() {
        tracer = new SkywalkingTracer();
    }


    @Override
    public Tracer getTracer() {
        return tracer;
    }
}
