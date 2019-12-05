/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
        mappings.put("mp4", "video/mpeg4");
        mappings.put("mpeg", "video/mpg");
        mappings.put("mps", "video/x-mpeg");
        mappings.put("mpv", "video/mpg");
        mappings.put("mpa", "video/x-mpg");
        mappings.put("mpe", "video/x-mpg");
        mappings.put("m4e", "video/mpeg4");
        mappings.put("m2v", "video/x-mpeg");


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
