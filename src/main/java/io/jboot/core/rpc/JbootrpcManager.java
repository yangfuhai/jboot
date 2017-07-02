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
package io.jboot.core.rpc;

import io.jboot.Jboot;
import io.jboot.core.mq.JbootmqMessageListener;
import io.jboot.core.spi.JbootSpiManager;
import io.jboot.event.JbootEventListener;
import io.jboot.exception.JbootAssert;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.core.rpc.grpc.JbootGrpc;
import io.jboot.core.rpc.local.JbootLocalrpc;
import io.jboot.core.rpc.motan.JbootMotanrpc;
import io.jboot.core.rpc.thrift.JbootThriftrpc;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import java.io.Serializable;
import java.util.List;


public class JbootrpcManager {
    private static JbootrpcManager manager;

    private JbootrpcManager() {
        config = Jboot.config(JbootrpcConfig.class);
    }

    public static JbootrpcManager me() {
        if (manager == null) {
            manager = ClassNewer.singleton(JbootrpcManager.class);
        }
        return manager;
    }


    private Jbootrpc jbootrpc;
    private JbootrpcConfig config;

    public Jbootrpc getJbootrpc() {
        if (jbootrpc == null) {
            jbootrpc = createJbootrpc();
        }
        return jbootrpc;
    }

    static Class[] default_excludes = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};


    public void autoExport() {
        List<Class> classes = ClassScanner.scanClass(true);
        if (ArrayUtils.isNullOrEmpty(classes)) {
            return;
        }

        for (Class clazz : classes) {
            JbootrpcService rpcService = (JbootrpcService) clazz.getAnnotation(JbootrpcService.class);
            if (rpcService == null) continue;

            String group = StringUtils.isBlank(rpcService.group()) ? config.getDefaultGroup() : rpcService.group();
            String version = StringUtils.isBlank(rpcService.version()) ? config.getDefaultVersion() : rpcService.version();
            int port = rpcService.port() <= 0 ? Integer.valueOf(config.getDefaultPort()) : rpcService.port();

            Class[] inters = clazz.getInterfaces();
            JbootAssert.assertFalse(inters == null || inters.length == 0,
                    String.format("class[%s] has no interface, can not use @JbootrpcService", clazz));

            Class[] excludes = ArrayUtils.concat(default_excludes, rpcService.exclude());
            for (Class inter : inters) {
                boolean exclude = false;
                for (Class ex : excludes) {
                    if (ex == inter) exclude = true;
                }
                if (exclude) continue;
                getJbootrpc().serviceExport(inter, Jboot.getInjector().getInstance(clazz), group, version, port);
            }
        }
    }


    private Jbootrpc createJbootrpc() {

        switch (config.getType()) {
            case JbootrpcConfig.TYPE_MOTAN:
                return new JbootMotanrpc();
            case JbootrpcConfig.TYPE_GRPC:
                return new JbootGrpc();
            case JbootrpcConfig.TYPE_THRIFT:
                return new JbootThriftrpc();
            case JbootrpcConfig.TYPE_LOCAL:
                return new JbootLocalrpc();
            default:
                return JbootSpiManager.me().spi(Jbootrpc.class, config.getType());
        }
    }
}
