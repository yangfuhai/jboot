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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import io.jboot.Jboot;
import io.jboot.utils.StringUtils;

import java.util.*;

/**
 * 定时读取远程配置信息
 *
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.config
 */
public abstract class ConfigRemoteReader {

    public static final String ACTION_ADD = "add";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_UPDATE = "update";

    private Timer timer;
    private TimerTask task;
    private String url;
    private int interval;
    private boolean running = false;

    // key : id , value : version
    private final Map<String, String> preScan = new HashMap<>();
    private final Map<String, String> curScan = new HashMap<>();


    private final PropInfos remoteProps = new PropInfos();
    private final Properties remoteProperties = new Properties();

    public ConfigRemoteReader(String url, int interval) {
        this.url = url;
        this.interval = interval;

        initRemoteProps();
    }

    private void initRemoteProps() {
        String jsonString = Jboot.httpGet(url);

        if (StringUtils.isBlank(jsonString)) {
            LogKit.error("can not get remote config info,plase check url : " + url);
            return;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(jsonString);
        } catch (Throwable ex) {
            LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + url, ex);
            return;
        }

        for (String key : jsonObject.keySet()) {
            JSONObject propInfoObject = jsonObject.getJSONObject(key);
            String version = propInfoObject.getString("version");
            JSONObject propertiesObject = propInfoObject.getJSONObject("properties");

            Properties properties = new Properties();
            for (String propertieKey : propertiesObject.keySet()) {
                properties.put(propertieKey, propertiesObject.getString(propertieKey));
                remoteProperties.put(propertieKey, propertiesObject.getString(propertieKey));
            }
            remoteProps.put(key, new PropInfos.PropInfo(version, properties));
        }
    }

    public abstract void onChange(String key, String oldValue, String newValue);

    private void working() {
        scan();
        compare();

        preScan.clear();
        preScan.putAll(curScan);
        curScan.clear();
    }

    private void scan() {

        String listUrl = url + "/list";
        String jsonString = Jboot.httpGet(listUrl);

        if (StringUtils.isBlank(jsonString)) {
            LogKit.error("can not get remote config info,plase check url : " + listUrl);
            return;
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonString);
        } catch (Throwable ex) {
            LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + listUrl, ex);
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            curScan.put(jsonObject.getString("id"), jsonObject.getString("version"));
        }

    }

    private void compare() {

        List<String> changesIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : curScan.entrySet()) {
            String version = entry.getValue();
            if (preScan.get(entry.getKey()) == null) {
                //新添加的文件
                changesIds.add(entry.getKey());
            } else if (!version.equals(preScan.get(entry.getKey()))) {
                //文件被修改了
                changesIds.add(entry.getKey());
            }
        }

        List<String> deleteIds = new ArrayList<>();
        for (Map.Entry<String, String> entry : preScan.entrySet()) {
            if (curScan.get(entry.getKey()) == null) {
                deleteIds.add(entry.getKey());
            }
        }

        for (String changeId : changesIds) {
            String url = this.url + "/" + changeId;
            String jsonString = Jboot.httpGet(url);

            if (StringUtils.isBlank(jsonString)) {
                LogKit.error("can not get remote config info,plase check url : " + url);
                continue;
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(jsonString);
            } catch (Throwable ex) {
                LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + url, ex);
                continue;
            }

            for (String key : jsonObject.keySet()) {
                JSONObject propInfoObject = jsonObject.getJSONObject(key);
                String version = propInfoObject.getString("version");
                JSONObject propertiesObject = propInfoObject.getJSONObject("properties");

                Properties properties = new Properties();
                for (String propertieKey : propertiesObject.keySet()) {
                    properties.put(propertieKey, propertiesObject.getString(propertieKey));
                }

                PropInfos.PropInfo newPropInfo = new PropInfos.PropInfo(version, properties);
                PropInfos.PropInfo localPropInfo = remoteProps.get(key);
                remoteProps.put(key, newPropInfo);

                for (Object newKey : newPropInfo.getProperties().keySet()) {
                    String localValue = localPropInfo.getString(newKey);
                    String remoteValue = newPropInfo.getString(newKey);
                    remoteProperties.put(newKey.toString(), remoteValue);
                    if (localValue == null && StringUtils.isNotBlank(remoteValue)) {
                        onChange(newKey.toString(), null, remoteValue);
                    } else if (!localValue.equals(remoteValue)) {
                        onChange(newKey.toString(), localValue, remoteValue);
                    }
                }

                for (Object localKey : localPropInfo.getProperties().keySet()) {
                    if (newPropInfo.getString(localKey) == null) {
                        remoteProperties.remove(localKey);
                        onChange(localKey.toString(), localPropInfo.getString(localKey), null);
                    }
                }

            }
        }

        for (String deleteId : deleteIds) {
            PropInfos.PropInfo propInfo = remoteProps.get(deleteId);
            for (Object key : propInfo.getProperties().keySet()) {
                remoteProperties.remove(key);
                onChange(key.toString(), propInfo.getString(key), null);
            }
        }
    }

    public void start() {
        if (!running) {
            timer = new Timer("Jboot-Config-Remote-Reader", true);
            task = new TimerTask() {
                public void run() {
                    working();
                }
            };
            timer.schedule(task, 0, 1010L * interval);
            running = true;
        }
    }

    public void stop() {
        if (running) {
            timer.cancel();
            task.cancel();
            running = false;
        }
    }


    public PropInfos getRemoteProps() {
        return remoteProps;
    }

    public Properties getRemoteProperties() {
        return remoteProperties;
    }
}


