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
package io.jboot.web.directive;

import io.jboot.utils.StringUtils;
import io.jboot.web.RequestManager;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.PaginateDirectiveBase;

import javax.servlet.http.HttpServletRequest;

@JFinalDirective("JbootPaginateDirective")
public class JbootPaginateDirective extends PaginateDirectiveBase {

    @Override
    protected String getUrl(int pageNumber) {
        HttpServletRequest request = RequestManager.me().getRequest();
        String queryString = request.getQueryString();
        String url = request.getRequestURI();
        if (StringUtils.isNotBlank(queryString)) {
            url = url.concat("?").concat(queryString);
        }
        return replaceUrlValue(url, "page", pageNumber);
    }


    public String replaceUrlValue(String url, String name, int value) {
        int index = url.indexOf(name + "=");
        if (index != -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(url.substring(0, index)).append(name + "=").append(value);
            int idx = url.indexOf("&", index);
            if (idx != -1) {
                sb.append(url.substring(idx));
            }
            url = sb.toString();
        } else {
            if (url.contains("?")) {
                url = url.concat(String.format("&page=%s", value));
            } else {
                url = url.concat(String.format("?page=%s", value));
            }
        }
        return url;
    }

}