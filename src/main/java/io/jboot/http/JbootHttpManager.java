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
package io.jboot.http;

import io.jboot.http.jboot.JbootHttpImpl;
import io.jboot.utils.ClassNewer;

public class JbootHttpManager {

    private static JbootHttpManager manager;


    public static JbootHttpManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootHttpManager.class);
        }
        return manager;
    }


    private JbootHttp jbootHttp;

    public JbootHttp getJbootHttp() {
        if (jbootHttp == null) {
            jbootHttp = new JbootHttpImpl();
        }
        return jbootHttp;
    }


}
