/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.config.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import io.jboot.config.PropInfoMap;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jboot.core.http.jboot.JbootHttpImpl;
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

    private Timer timer;
    private TimerTask task;
    private String url;
    private String name;
    private int interval;
    private boolean running = false;

    // key : id , value : version
    private final Map<String, String> preScan = new HashMap<>();
    private final Map<String, String> curScan = new HashMap<>();


    private final PropInfoMap remotePropInfoMap = new PropInfoMap();
    private final Properties remoteProperties = new Properties();

    private final JbootHttpImpl http = new JbootHttpImpl();

    public ConfigRemoteReader(String url, String name, int interval) {
        this.url = url;
        this.name = name;
        this.interval = interval;

        initRemoteProps();
    }

    private String httpGet(String url) {
        JbootHttpRequest request = JbootHttpRequest.create(url, null, JbootHttpRequest.METHOD_GET);
        JbootHttpResponse response = http.handle(request);
        return response.isError() ? null : response.getContent();
    }

    /**
     * 初始化远程配置信息
     */
    private void initRemoteProps() {
        String jsonString = httpGet(url+"/"+name);

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

        //先清空本地数据，initRemoteProps 可能被多次调用
        remoteProperties.clear();
        remotePropInfoMap.clear();

        for (String key : jsonObject.keySet()) {
            JSONObject propInfoObject = jsonObject.getJSONObject(key);
            String version = propInfoObject.getString("version");
            JSONObject propertiesObject = propInfoObject.getJSONObject("properties");

            Properties properties = new Properties();
            for (String propertieKey : propertiesObject.keySet()) {
                properties.put(propertieKey, propertiesObject.getString(propertieKey));
                remoteProperties.put(propertieKey, propertiesObject.getString(propertieKey));
            }
            remotePropInfoMap.put(key, new PropInfoMap.PropInfo(version, properties));
        }
    }

    public abstract void onChange(String key, String oldValue, String newValue);


    private int scanFailTimes = 0;

    private void working() {

        boolean scanSuccess = scan();

        //扫描失败
        if (!scanSuccess) {

            // 可能是服务挂了
            if (scanFailTimes++ > 5) {
                remoteProperties.clear();
                remotePropInfoMap.clear();
            }
        } else {

            if (scanFailTimes >= 5) {
                initRemoteProps();
            }

            scanFailTimes = 0;
            compare();

            preScan.clear();
            preScan.putAll(curScan);
            curScan.clear();
        }


    }

    /**
     * 定时扫描远程配置信息
     */
    private boolean scan() {

        String listUrl = url + "/list";
        String jsonString = httpGet(listUrl);

        if (StringUtils.isBlank(jsonString)) {
            LogKit.error("can not get remote config info,plase check url : " + listUrl);
            return false;
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonString);
        } catch (Throwable ex) {
            LogKit.error("can not parse json : \n" + jsonString + "\n\nfrom url : " + listUrl, ex);
            return false;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            curScan.put(jsonObject.getString("id"), jsonObject.getString("version"));
        }

        return true;

    }

    private void compare() {

        //记录被修改或者新增的文件ID
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

        //记录被删除的文件id
        List<String> deleteIds = new ArrayList<>();
        for (Map.Entry<String, String> entry : preScan.entrySet()) {
            if (curScan.get(entry.getKey()) == null) {
                deleteIds.add(entry.getKey());
            }
        }

        // 有文件 被修改 或者新增了
        for (String changeId : changesIds) {
            String url = this.url + "/" + changeId;
            String jsonString = httpGet(url);

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

                PropInfoMap.PropInfo newPropInfo = new PropInfoMap.PropInfo(version, properties);
                PropInfoMap.PropInfo localPropInfo = remotePropInfoMap.get(key);
                remotePropInfoMap.put(key, newPropInfo);

                if(localPropInfo==null)
                    continue;

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

        /**
         * 有文件被删除了
         */
        for (String deleteId : deleteIds) {
            PropInfoMap.PropInfo propInfo = remotePropInfoMap.get(deleteId);
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


    public Properties getRemoteProperties() {
        return remoteProperties;
    }
}


