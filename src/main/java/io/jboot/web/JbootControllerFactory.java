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
package io.jboot.web;

import com.jfinal.core.Controller;
import com.jfinal.core.ControllerFactory;
import io.jboot.Jboot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web
 */
public class JbootControllerFactory extends ControllerFactory {

    private ThreadLocal<Map<Class<? extends Controller>, Controller>> buffers = new ThreadLocal<Map<Class<? extends Controller>, Controller>>() {
        protected Map<Class<? extends Controller>, Controller> initialValue() {
            return new HashMap<Class<? extends Controller>, Controller>();
        }
    };

    public Controller getController(Class<? extends Controller> controllerClass) throws InstantiationException, IllegalAccessException {
        Controller ret = buffers.get().get(controllerClass);
        if (ret == null) {
            ret = controllerClass.newInstance();
            Jboot.injectMembers(ret);
            buffers.get().put(controllerClass, ret);
        }
        return ret;
    }
}