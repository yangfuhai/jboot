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
package io.jboot.aop.injector;

import com.google.inject.MembersInjector;
import com.jfinal.log.Log;
import io.jboot.Jboot;

import java.lang.reflect.Field;

/**
 * RPC 的注入器，用来初始化RPC对象
 */
public class JbootrpcMembersInjector implements MembersInjector {

    private static Log log = Log.getLog(JbootrpcMembersInjector.class);

    private Field field;

    public JbootrpcMembersInjector(Field field) {
        this.field = field;
    }

    @Override
    public void injectMembers(Object instance) {
        Object rpcImpl = null;

        try {
            rpcImpl = Jboot.service(field.getType());
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        if (rpcImpl == null) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(instance, rpcImpl);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }
    }
}
