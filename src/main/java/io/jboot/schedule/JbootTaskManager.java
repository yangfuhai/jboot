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

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import io.jboot.Jboot;
import io.jboot.schedule.annotation.Cron4jTask;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;

import java.util.List;


public class JbootTaskManager {

    private static JbootTaskManager manager;
    private Cron4jPlugin cron4jPlugin;

    private JbootTaskManager() {

        if (!isCron4jEnable()) {
            return;
        }

        initCron4jPlugin();
    }


    public static final JbootTaskManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootTaskManager.class);
        }
        return manager;
    }

    private void initCron4jPlugin() {

        Prop prop = null;
        try {
            prop = PropKit.use(Jboot.getJbootConfig().getCron4jFile());
        } catch (Throwable ex) {
        }

        cron4jPlugin = prop == null ? new Cron4jPlugin() : new Cron4jPlugin(prop);

        List<Class<Runnable>> list = ClassScanner.scanSubClass(Runnable.class);
        if (ArrayUtils.isNullOrEmpty(list)) {
            return;
        }

        for (Class<Runnable> clazz : list) {
            Cron4jTask cron4jTask = clazz.getAnnotation(Cron4jTask.class);
            if (cron4jTask == null) continue;
            cron4jPlugin.addTask(cron4jTask.cron(), ClassNewer.newInstance(clazz), cron4jTask.daemon());
        }
    }

    public Cron4jPlugin getCron4jPlugin() {
        return cron4jPlugin;
    }

    public boolean isCron4jEnable() {
        return Jboot.getJbootConfig().isCron4jEnable();
    }


}
