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
package io.jboot.component.shiro.processer;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;


public class ShiroRequiresPermissionsProcesser implements IShiroAuthorizeProcesser {
    
    private final RequiresPermissions requiresPermissions;

    public ShiroRequiresPermissionsProcesser(RequiresPermissions requiresPermissions) {
        this.requiresPermissions = requiresPermissions;
    }

    @Override
    public AuthorizeResult authorize() {
        try {
            String[] perms = requiresPermissions.value();
            Subject subject = SecurityUtils.getSubject();

            if (perms.length == 1) {
                subject.checkPermission(perms[0]);
                return AuthorizeResult.ok();
            }
            if (Logical.AND.equals(requiresPermissions.logical())) {
                subject.checkPermissions(perms);
                return AuthorizeResult.ok();
            }
            if (Logical.OR.equals(requiresPermissions.logical())) {
                // Avoid processing exceptions unnecessarily - "delay" throwing the
                // exception by calling hasRole first
                boolean hasAtLeastOnePermission = false;
                for (String permission : perms)
                    if (subject.isPermitted(permission))
                        hasAtLeastOnePermission = true;
                // Cause the exception if none of the role match, note that the
                // exception message will be a bit misleading
                if (!hasAtLeastOnePermission)
                    subject.checkPermission(perms[0]);

            }

            return AuthorizeResult.ok();

        } catch (AuthorizationException e) {
            return AuthorizeResult.fail(AuthorizeResult.ERROR_CODE_UNAUTHORIZATION);
        }
    }
}
