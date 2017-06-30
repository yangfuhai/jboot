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
package io.jboot.component.shiro;

import io.jboot.component.shiro.processer.AuthorizeResult;
import io.jboot.component.shiro.processer.IShiroAuthorizeProcesser;

import java.util.ArrayList;
import java.util.List;

/**
 * Shiro 认证处理器 执行者，用于对
 */
public class ShiroAuthorizeProcesserInvoker {

    List<IShiroAuthorizeProcesser> processers;

    public void addProcesser(IShiroAuthorizeProcesser processer) {
        if (processers == null) {
            processers = new ArrayList<>();
        }
        if (!processers.contains(processer)) {
            processers.add(processer);
        }
    }

    public List<IShiroAuthorizeProcesser> getProcessers() {
        return processers;
    }

    public AuthorizeResult invoke() {
        if (processers == null || processers.size() == 0) {
            return AuthorizeResult.ok();
        }

        for (IShiroAuthorizeProcesser processer : processers) {
            AuthorizeResult result = processer.authorize();

            if (result.isFail()) {
                return result;
            }
        }

        return AuthorizeResult.ok();
    }


}
