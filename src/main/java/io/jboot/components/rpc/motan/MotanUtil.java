/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.rpc.motan;

import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/20
 */
public class MotanUtil {


    public static RefererConfig toRefererConfig(JbootrpcReferenceConfig rc){
        RefererConfig referenceConfig = new RefererConfig();
        Field[] fields = rc.getClass().getDeclaredFields();
        for (Field field : fields){
            try {
                Method method = RefererConfig.class.getDeclaredMethod("set"+ StrUtil.firstCharToUpperCase(field.getName()),field.getType());
                field.setAccessible(true);
                method.invoke(referenceConfig,field.get(rc));
            } catch (Exception e) {
                // ignore
            }
        }
        return referenceConfig;
    }



    public static ServiceConfig toServiceConfig(JbootrpcServiceConfig sc){
        ServiceConfig serviceConfig = new ServiceConfig();
        Field[] fields = sc.getClass().getDeclaredFields();
        for (Field field : fields){
            try {
                Method method = ServiceConfig.class.getDeclaredMethod("set"+StrUtil.firstCharToUpperCase(field.getName()),field.getType());
                field.setAccessible(true);
                method.invoke(serviceConfig,field.get(sc));
            } catch (Exception e) {
                // ignore
            }
        }
        return serviceConfig;
    }
}
