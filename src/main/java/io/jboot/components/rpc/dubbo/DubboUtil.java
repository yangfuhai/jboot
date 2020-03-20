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
package io.jboot.components.rpc.dubbo;


import io.jboot.Jboot;
import io.jboot.components.rpc.config.ApplicationConfig;
import io.jboot.components.rpc.config.ConsumerConfig;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/19
 */
 class DubboUtil {

     public static org.apache.dubbo.config.ApplicationConfig getApplicationConfig(){
         ApplicationConfig app = Jboot.config(ApplicationConfig.class,"jboot.rpc.application");

         org.apache.dubbo.config.ApplicationConfig dubboConfig = new org.apache.dubbo.config.ApplicationConfig();

         dubboConfig.setQosEnable(app.getQosEnable());
         dubboConfig.setQosAcceptForeignIp(app.getQosAcceptForeignIp());
         dubboConfig.setQosHost(app.getQosHost());
         dubboConfig.setQosPort(app.getQosPort());
         dubboConfig.setName(app.getName());
         dubboConfig.setArchitecture(app.getArchitecture());
         dubboConfig.setCompiler(app.getCompiler());
         dubboConfig.setDefault(app.getDefault());
         dubboConfig.setDumpDirectory(app.getDumpDirectory());
         dubboConfig.setEnvironment(app.getEnvironment());
         dubboConfig.setLogger(app.getLogger());
         dubboConfig.setMetadataType(app.getMetadataType());
         dubboConfig.setMonitor(app.getMonitor());
         dubboConfig.setOrganization(app.getOrganization());
         dubboConfig.setRegistryIds(app.getRegistryIds());
         dubboConfig.setRegisterConsumer(app.getRegisterConsumer());
         dubboConfig.setRepository(app.getRepository());
         dubboConfig.setShutwait(app.getShutwait());
         dubboConfig.setOwner(app.getOwner());
         dubboConfig.setVersion(app.getVersion());

         return dubboConfig;
     }



    public static org.apache.dubbo.config.ConsumerConfig getConsumerConfig(){
        ConsumerConfig app = Jboot.config(ConsumerConfig.class,"jboot.rpc.consumer");

        org.apache.dubbo.config.ConsumerConfig dubboConfig = new org.apache.dubbo.config.ConsumerConfig();
        dubboConfig.setClient(app.getClient());
        dubboConfig.setCorethreads(app.getCorethreads());
        dubboConfig.setDefault(app.getDefault());
        dubboConfig.setQueues(app.getQueues());
        dubboConfig.setShareconnections(app.getShareconnections());
        dubboConfig.setThreadpool(app.getThreadpool());
        dubboConfig.setThreads(app.getThreads());
//        dubboConfig.setTimeout(app.gett);


        return dubboConfig;
    }
}
