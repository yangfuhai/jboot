/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.swagger;

import io.jboot.Jboot;
import io.jboot.utils.ClassScanner;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;

import java.util.List;

import static io.swagger.models.Scheme.HTTP;
import static io.swagger.models.Scheme.HTTPS;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 * <p>
 * 相关文档: https://www.gitbook.com/book/huangwenchao/swagger/details
 */
public class JbootSwaggerManager {


    private JbootSwaggerConfig config = Jboot.config(JbootSwaggerConfig.class);
    private Swagger swagger;
    private static JbootSwaggerManager instance;

    public static JbootSwaggerManager me() {
        if (instance == null) {
            instance = new JbootSwaggerManager();
        }

        return instance;
    }


    public void init() {
        if (!config.isConfigOk()) {
            return;
        }

        swagger = new Swagger();
        swagger.setHost(config.getHost());
        swagger.setBasePath("/");
        swagger.addScheme(HTTP);
        swagger.addScheme(HTTPS);


        Info swaggerInfo = new Info();
        swaggerInfo.setDescription(config.getDescription());
        swaggerInfo.setVersion(config.getVersion());
        swaggerInfo.setTitle(config.getTitle());
        swaggerInfo.setTermsOfService(config.getTermsOfService());

        Contact contact = new Contact();
        contact.setName(config.getContactName());
        contact.setEmail(config.getContactEmail());
        contact.setUrl(config.getContactUrl());
        swaggerInfo.setContact(contact);

        License license = new License();
        license.setName(config.getLicenseName());
        license.setUrl(config.getLicenseUrl());
        swaggerInfo.setLicense(license);


        swagger.setInfo(swaggerInfo);

        List<Class> classes = ClassScanner.scanClassByAnnotation(RequestMapping.class, false);

        Reader.read(swagger, classes);

    }


    public Swagger getSwagger() {
        return swagger;
    }
}
