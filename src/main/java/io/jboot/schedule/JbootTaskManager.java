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
import io.jboot.exception.JbootException;
import io.jboot.schedule.annotation.Cron4jTask;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import it.sauronsoftware.cron4j.ProcessTask;
import it.sauronsoftware.cron4j.Task;

import java.util.List;


public class JbootTaskManager {

    private static JbootTaskManager manager;
    private Cron4jPlugin cron4jPlugin;

    public JbootTaskManager() {
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
            prop = PropKit.use(Jboot.me().getJbootConfig().getCron4jFile());
        } catch (Throwable ex) {
        }

        cron4jPlugin = prop == null ? new Cron4jPlugin() : new Cron4jPlugin(prop);

        List<Class> list = ClassScanner.scanClassByAnnotation(Cron4jTask.class, true);
        if (ArrayUtils.isNullOrEmpty(list)) {
            return;
        }

        for (Class clazz : list) {
            Cron4jTask cron4jTask = (Cron4jTask) clazz.getAnnotation(Cron4jTask.class);
            if (clazz == Runnable.class) {
                cron4jPlugin.addTask(cron4jTask.cron(), (Runnable) ClassNewer.newInstance(clazz), cron4jTask.daemon());
            } else if (clazz == ProcessTask.class) {
                cron4jPlugin.addTask(cron4jTask.cron(), (ProcessTask) ClassNewer.newInstance(clazz), cron4jTask.daemon());
            } else if (clazz == Task.class) {
                cron4jPlugin.addTask(cron4jTask.cron(), (Task) ClassNewer.newInstance(clazz), cron4jTask.daemon());
            } else {
                throw new JbootException("annotation Cron4jTask can not use for class : " + clazz);
            }
        }
    }

    public Cron4jPlugin getCron4jPlugin() {
        return cron4jPlugin;
    }

    public boolean isCron4jEnable() {
        return Jboot.me().getJbootConfig().isCron4jEnable();
    }


}
