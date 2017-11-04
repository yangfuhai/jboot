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
package io.jboot.component.swagger;

import io.jboot.Jboot;
import io.jboot.component.swagger.annotation.SwaggerAPIs;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 */
public class SwaggerManager {

    private static SwaggerManager me = new SwaggerManager();

    public static SwaggerManager me() {
        return me;
    }

    private JbootSwaggerConfig config = Jboot.config(JbootSwaggerConfig.class);

    private Swagger swagger;

    private SwaggerManager() {
        if (!config.isConfigOk()) {
            return;
        }

        initSwagger();
    }


    private void initSwagger() {
        this.swagger = new Swagger();
        this.swagger.setHost(config.getHost());

        SwaggerInfo info = new SwaggerInfo();
        info.setTitle(config.getTitle());
        info.setDescription(config.getDescription());
        info.setTermsOfService(config.getTermsOfService());
        info.setVersion(config.getVersion());

        if (StringUtils.isNotBlank(config.getContact())) {
            String[] contactInfos = config.getContact().split(";");
            for (String contactInfo : contactInfos) {
                String[] contancts = contactInfo.split(":");
                if (contancts.length != 2) {
                    continue;
                }
                info.addContact(contancts[0], contancts[1]);
            }
        }
        this.swagger.setInfo(info);


        List<SwaggerTag> tagList = new ArrayList<>();


        List<Class> classes = ClassScanner.scanClassByAnnotation(SwaggerAPIs.class, false);
        for (Class controllerClass : classes) {
            initSwaggerTag(tagList, controllerClass);
        }

        this.swagger.setTags(tagList.toArray(new SwaggerTag[]{}));

    }

    private void initSwaggerTag(List<SwaggerTag> tagList, Class controllerClass) {
        SwaggerAPIs apis = (SwaggerAPIs) controllerClass.getAnnotation(SwaggerAPIs.class);
        SwaggerTag tag = new SwaggerTag();
        tag.setName(apis.name());
        tag.setDescription(apis.description());

        if (StringUtils.isNotBlank(apis.externalDescription())) {
            tag.addExternalDoc("description", apis.externalDescription());
        }
        if (StringUtils.isNotBlank(apis.externalUrl())) {
            tag.addExternalDoc("url", apis.externalUrl());
        }
        tagList.add(tag);
    }


    public Swagger getSwagger() {
        return swagger;
    }
}
