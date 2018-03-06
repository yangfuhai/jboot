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
//import org.skywalking.apm.toolkit.opentracing.SkywalkingTracer;

public class SkywalkingTracerFactory implements TracerFactory {


    private Tracer tracer;

    public SkywalkingTracerFactory() {

        // doc : https://github.com/apache/incubator-skywalking/blob/master/docs/cn/skywalking-opentracing-CN.md
//        tracer = new SkywalkingTracer();
    }


    @Override
    public Tracer getTracer() {
        return tracer;
    }
}
