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
package io.jboot.config;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import io.jboot.Jboot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 配置管理类
 * <p>
 * 用于读取配置信息，包括本地配置信息和分布式远程配置信息
 */
public class JbootConfigManager {

    private static JbootConfigManager me = new JbootConfigManager();

    public static JbootConfigManager me() {
        return me;
    }

    private JbootConfigConfig config = Jboot.config(JbootConfigConfig.class);
    private HashMap<String, Prop> configProps = new HashMap<>();

    private Timer timer;
    private TimerTask timerTask;
    private ConfigServerScanner serverScanner;

    public void init() {
        if (config.isServerEnable()) {
            initConfigProps();
            initConfigServerScanner();
        }

        /**
         * 定时获取远程服务配置信息
         */
        else if (config.isRemoteEnable()) {
            timer = new Timer("Jboot-Config-remoteGetter", true);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    doRemoteGet();
                }
            };
        }
    }


    private void doRemoteGet() {

    }

    private void initConfigProps() {

        String paths = config.getPath();

        if (!paths.contains(";")) {
            initConfig(new File(paths));
        } else {
            String[] pathss = paths.split(";");
            for (String path : pathss) {
                initConfig(new File(path));
            }
        }
    }


    private void initConfig(File file) {
        if (file == null || !file.exists())
            return;

        if (file.isFile() && file.getName().toLowerCase().endsWith(".properties")) {
            try {
                configProps.put(file.getCanonicalPath(), PropKit.use(file));
            } catch (IOException e) {
                LogKit.error(e.getMessage(), e);
            }
        } else if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                for (File f : fs) {
                    initConfig(f);
                }
            }
        }
    }


    private void initConfigServerScanner() {
        serverScanner = new ConfigServerScanner(config.getPath(), 5) {
            @Override
            public void onChange(String action, String file) {
                switch (action) {
                    case ConfigServerScanner.ACTION_ADD:
                        configProps.put(file, PropKit.use(new File(file)));
                        break;
                    case ConfigServerScanner.ACTION_DELETE:
                        configProps.remove(file);
                        break;
                    case ConfigServerScanner.ACTION_UPDATE:
                        PropKit.useless(file);
                        configProps.put(file, PropKit.use(new File(file)));
                        break;
                }
            }
        };

        serverScanner.start();
    }

    public HashMap<String, Prop> getConfigProps() {
        return configProps;
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (serverScanner != null) {
            serverScanner.stop();
        }
    }

}
