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
 * Hystrix 的注入器，用来初始化使用@EnableHystrixCommand注解的对象
 */
public class JbootHystrixInjector implements MembersInjector {

    private static Log log = Log.getLog(JbootrpcMembersInjector.class);

    private Field field;

    public JbootHystrixInjector(Field field) {
        this.field = field;
    }

    @Override
    public void injectMembers(Object instance) {
        /**
         * 给带注解的field强行添加某个拦截器，必须通过自定义的Injector来初始化field实例
         * 否则只能给field的类添加注释，然后通过bind 和 annotation去找到
         *
         * 例如：
         *
         * controller{
         *
         *      @useAAA
         *      Service server1;
         * }
         *
         * 如果service类本身没有任何注解，同时想给server1添加注释，让service1实现监听或其他行为，
         * 需自己来给service1进行初始化（自定义Injectro），而不能用默认，同时在初始化的过程中添加上监听器
         *
         *
         */
        Object o = Jboot.getInjector().getInstance(field.getType());

        try {
            field.setAccessible(true);
            field.set(instance, o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
