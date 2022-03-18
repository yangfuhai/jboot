/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtil {

    public static String getLocalIpAddress() {
        String hostIpAddress = null;
        String siteLocalIpAddress = null;// 外网IP
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            boolean findSiteLocalIpAddress = false;// 是否找到外网IP
            while (networkInterfaces.hasMoreElements() && !findSiteLocalIpAddress) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();

                    if (!address.isSiteLocalAddress() && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 外网IP
                        siteLocalIpAddress = address.getHostAddress();
                        findSiteLocalIpAddress = true;
                        break;
                    } else if (address.isSiteLocalAddress()
                            && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 内网IP
                        hostIpAddress = address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 优先使用配置的外网IP地址
        return StrUtil.isNotBlank(siteLocalIpAddress) ? siteLocalIpAddress : hostIpAddress;
    }

}
