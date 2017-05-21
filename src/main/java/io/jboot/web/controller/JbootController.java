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
package io.jboot.web.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * Created by michael on 17/3/21.
 */
public class JbootController extends Controller {

    /**
     * 是否是手机浏览器
     *
     * @return
     */
    public boolean isMoblieBrowser() {
        return RequestUtils.isMoblieBrowser(getRequest());
    }

    /**
     * 是否是微信浏览器
     *
     * @return
     */
    public boolean isWechatBrowser() {
        return RequestUtils.isWechatBrowser(getRequest());
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    public boolean isIEBrowser() {
        return RequestUtils.isIEBrowser(getRequest());
    }

    public boolean isAjaxRequest() {
        return RequestUtils.isAjaxRequest(getRequest());
    }

    public boolean isMultipartRequest() {
        return RequestUtils.isMultipartRequest(getRequest());
    }


    @Before(NotAction.class)
    public String getIPAddress() {
        return RequestUtils.getIpAddress(getRequest());
    }

    @Before(NotAction.class)
    public String getReferer() {
        return RequestUtils.getReferer(getRequest());
    }


    @Before(NotAction.class)
    public String getUserAgent() {
        return RequestUtils.getUserAgent(getRequest());
    }

    @Before(NotAction.class)
    public String getBaseUrl() {
        HttpServletRequest req = getRequest();
        int port = req.getServerPort();

        return port == 80 ? String.format("%s://%s%s", req.getScheme(), req.getServerName(), req.getContextPath())
                : String.format("%s://%s%s%s", req.getScheme(), req.getServerName(), ":" + port, req.getContextPath());

    }


    public HashMap<String, UploadFile> getUploadFilesMap() {
        if (!isMultipartRequest()) {
            return null;
        }

        List<UploadFile> fileList = getFiles();
        HashMap<String, UploadFile> filesMap = null;
        if (ArrayUtils.isNotEmpty(fileList)) {
            filesMap = new HashMap<String, UploadFile>();
            for (UploadFile ufile : fileList) {
                filesMap.put(ufile.getParameterName(), ufile);
            }
        }
        return filesMap;
    }


}
