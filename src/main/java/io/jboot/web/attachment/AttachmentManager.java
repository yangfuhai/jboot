/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jboot.web.attachment;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class AttachmentManager {


    private static final AttachmentManager ME = new AttachmentManager();

    public static AttachmentManager me() {
        return ME;
    }

    private AttachmentManager() {
        containerMap.put("default",new LocalAttachmentContainer());
    }


    private Map<String,AttachmentContainer> containerMap = new ConcurrentHashMap<>();


    public AttachmentContainer matchContainer(String target, HttpServletRequest request){
        if (containerMap.size() == 0){
            return null;
        }

        for (AttachmentContainer container : containerMap.values()){
            if (container.matchFile(target,request)){
                return container;
            }
        }

        return null;
    }


    public void addContainer(String name,AttachmentContainer container){
        containerMap.put(name,container);
    }

    public AttachmentContainer getContainer(String name){
        return getContainer(name);
    }

    public Map<String, AttachmentContainer> getContainerMap() {
        return containerMap;
    }

    public AttachmentContainer getDefaultContainer(){
        AttachmentContainer defaultContainer = containerMap.get("default");
        if (defaultContainer != null){
            return defaultContainer;
        }

        if (containerMap.size() > 0){
            return containerMap.values().stream().findFirst().get();
        }

        return null;
    }
}
