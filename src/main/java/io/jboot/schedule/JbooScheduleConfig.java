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
package io.jboot.schedule;

import io.jboot.config.annotation.PropertieConfig;


@PropertieConfig(prefix = "jboot.schedule")
public class JbooScheduleConfig {
    private String cron4jFile = "cron4j.properties";
    private int poolSize = Runtime.getRuntime().availableProcessors() * 8;


    public String getCron4jFile() {
        return cron4jFile;
    }

    public void setCron4jFile(String cron4jFile) {
        this.cron4jFile = cron4jFile;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
