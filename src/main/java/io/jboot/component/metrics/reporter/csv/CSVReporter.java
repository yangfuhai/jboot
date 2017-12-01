/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.component.metrics.reporter.csv;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import io.jboot.Jboot;
import io.jboot.component.metrics.JbootMetricsReporter;
import io.jboot.utils.StringUtils;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.metrics.reporter.csv
 */
public class CSVReporter implements JbootMetricsReporter {



    @Override
    public void report(MetricRegistry metricRegistry) {

        JbootMetricsCVRReporterConfig cvrReporterConfig = Jboot.config(JbootMetricsCVRReporterConfig.class);

        if (StringUtils.isBlank(cvrReporterConfig.getPath())) {
            throw new NullPointerException("csv reporter path must not be null, please config jboot.metrics.reporter.cvr.path in you properties.");
        }

        final CsvReporter reporter = CsvReporter.forRegistry(metricRegistry)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File(cvrReporterConfig.getPath()));

        reporter.start(1, TimeUnit.SECONDS);
    }
}