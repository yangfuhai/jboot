/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.app;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.MimeMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/3
 */
public class HttpContentTypes {

    private static Map<String, String> mappings = new HashMap<>();

    static {

        /**
         * 视频相关
         */
        mappings.put("asf", "video/x-ms-asf");
        mappings.put("asx", "video/x-ms-asf");
        mappings.put("avi", "video/avi");
        mappings.put("flv", "video/x-flv");
        mappings.put("mp4", "video/mp4");
        mappings.put("mpeg", "video/mpg");
        mappings.put("mps", "video/x-mpeg");
        mappings.put("mpv", "video/mpg");
        mappings.put("mov", "video/quicktime");
        mappings.put("mpa", "video/x-mpg");
        mappings.put("mpe", "video/x-mpg");
        mappings.put("m4e", "video/mpeg4");
        mappings.put("m2v", "video/x-mpeg");
        mappings.put("wmv", "video/x-ms-wmv");
        mappings.put("3gp", "video/3gpp");
        mappings.put("ts", "video/MP2T");


        /**
         * 音频相关
         */
        mappings.put("mp3", "audio/mp3");
        mappings.put("mp2", "audio/mp2");
        mappings.put("m3u", "audio/x-mpegurl");
        mappings.put("m3u8", "audio/x-mpegurl");
        mappings.put("mpga", "audio/rn-mpeg");
        mappings.put("ra", "audio/vnd.rn-realaudio");
        mappings.put("ram", "audio/x-pn-realaudio");
        mappings.put("wav", "audio/wav");
        mappings.put("wax", "audio/x-ms-wax");
        mappings.put("wma", "audio/x-ms-wma");

        /**
         * 文档相关
         */
        mappings.put("pdf","application/pdf");
        mappings.put("xml","application/xml");
        mappings.put("json","application/json");
        mappings.put("doc","application/msword");
        mappings.put("docx","application/msword");
        mappings.put("xls","application/vnd.ms-excel");
        mappings.put("xlsx","application/vnd.ms-excel");
        mappings.put("pot","application/vnd.ms-powerpoint");
        mappings.put("ppt","application/vnd.ms-powerpoint");
    }

    /**
     * 让 undertow 支持音视频格式文件在线播放
     */
    public static void init(DeploymentInfo di) {
       for (Map.Entry<String,String> entry : mappings.entrySet()){
           di.addMimeMapping(new MimeMapping(entry.getKey(),entry.getValue()));
       }
    }

}
