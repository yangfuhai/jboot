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
package io.jboot.server;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.server.Scanner;

import java.io.File;


public class AutoDeployManager {

    private static AutoDeployManager manager = new AutoDeployManager();

    private AutoDeployManager() {
    }

    public static AutoDeployManager me() {
        return manager;
    }


    public void run() {

        File file = new File(PathKit.getRootClassPath()).getParentFile().getParentFile();
        Scanner scanner = new Scanner(file.getAbsolutePath(), 3) {
            public void onChange() {
                try {
                    System.err.println("file changes ......");
//                    UnderTowServer.start(UnderTowServer.createServer());
//                    System.err.println("Loading complete.");
                } catch (Exception e) {
                    System.err.println("Error reconfiguring/restarting webapp after change in watched files");
                    LogKit.error(e.getMessage(), e);
                }
            }
        };

        scanner.start();
    }

}
