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
package io.jboot.server.listener;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;


public interface JbootAppListener {

    public void onJfinalConstantConfig(Constants constants);

    public void onJfinalRouteConfig(Routes routes);

    public void onJfinalEngineConfig(Engine engine);

    public void onJfinalPluginConfig(JfinalPlugins plugins);

    public void onInterceptorConfig(Interceptors interceptors);

    public void onHandlerConfig(JfinalHandlers handlers);

    public void onJFinalStarted();

    public void onJFinalStop();

    public void onJbootStarted();

}
