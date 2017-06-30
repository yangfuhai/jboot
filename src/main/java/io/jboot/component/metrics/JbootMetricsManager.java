/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import io.jboot.Jboot;
import io.jboot.utils.ClassNewer;


public class JbootMetricsManager {

    private static JbootMetricsManager me;

    public static JbootMetricsManager me() {
        if (me == null) {
            me = ClassNewer.singleton(JbootMetricsManager.class);
        }
        return me;
    }

    private JbootMetricsManager() {
    }


    private MetricRegistry metricRegistry;
    private JbootMetricsConfig metricsConfig;



    public void init(){
        metricRegistry = new MetricRegistry();
        metricsConfig = Jboot.config(JbootMetricsConfig.class);

        /**
         * JMX报表,方便JConsole或者VisualVM查看查看
         */
        if (metricsConfig.isJmxReporter()) {
            JmxReporter.forRegistry(metricRegistry).build().start();
        }
    }


    public MetricRegistry metric() {
        return metricRegistry;
    }


}
