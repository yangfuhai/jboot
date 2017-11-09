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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import io.jboot.Jboot;
import io.jboot.component.swagger.annotation.*;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;
import io.jboot.web.ControllerManager;

import java.lang.reflect.Method;
import java.util.*;

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

            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                SwaggerAPI swaggerAPI = method.getAnnotation(SwaggerAPI.class);
                if (swaggerAPI == null) {
                    continue;
                }

                String methodPath = "index".equals(method.getName()) ? "" : "/" + method.getName();
                String pathString = ControllerManager.me().getPathByController(controllerClass) + methodPath;

                SwaggerPath path = new SwaggerPath();

                path.setMethod(swaggerAPI.method());
                path.setPath(pathString);
                path.setTags(tag.getName());
                path.setDescription(swaggerAPI.description());
                path.setOperationId(swaggerAPI.operationId());
                path.setSummary(swaggerAPI.summary());

                SwaggerParam[] swaggerParams = swaggerAPI.params();
                List<Map> parameters = Lists.newArrayList();
                for (SwaggerParam swaggerParam : swaggerParams) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("in", swaggerParam.in());
                    paramMap.put("name", swaggerParam.name());
                    paramMap.put("description", swaggerParam.description());
                    paramMap.put("required", swaggerParam.required());
                    paramMap.put("description", swaggerParam.description());

                    if (StringUtils.isNotBlank(swaggerParam.definition())) {
                        Map<String, String> schemaMap = Maps.newHashMap();
                        schemaMap.put("$ref", "#/definitions/" + swaggerParam.definition());
                        paramMap.put("schema", schemaMap);
                    }


                    parameters.add(paramMap);
                }

                SwaggerResponse response = swaggerAPI.response();
                Map response200 = Maps.newHashMap();
                response200.put("description", response.description200());
                if (StringUtils.isNotBlank(response.definitions200())) {
                    Map schemaMap = Maps.newHashMap();
                    schemaMap.put("$ref", "#/definitions/" + response.definitions200());
                    response200.put("schema", schemaMap);
                }


                Map response400 = Maps.newHashMap();
                response400.put("description", response.description400());

                Map response404 = Maps.newHashMap();
                response404.put("description", response.description404());

                path.addResponse("200", response200);
                path.addResponse("400", response400);
                path.addResponse("404", response404);

                path.setParameters(parameters);

                this.swagger.addPath(path.getPath(), path.toMap());

            }
        }

        this.swagger.setTags(tagList.toArray(new SwaggerTag[]{}));


        List<Class> definistionClasses = ClassScanner.scanClassByAnnotation(SwaggerDefinition.class, false);
        for (Class definistionClass : definistionClasses) {

            SwaggerDefinition sd = (SwaggerDefinition) definistionClass.getAnnotation(SwaggerDefinition.class);

            SwaggerDefinitionInfo sdi = new SwaggerDefinitionInfo();

            String name = StringUtils.isBlank(sd.value()) ? definistionClass.getSimpleName() : sd.value();
            sdi.setName(name);

            Map propertiesMap = Maps.newHashMap();
            Method[] methods = definistionClass.getMethods();
            for (Method method : methods) {
                if (!isGetMethod(method)) {
                    continue;
                }

                String filed = StrKit.firstCharToLowerCase(method.getName().substring(3));
                Class returnType = method.getReturnType();

                String[] strings = getSwaggerTypeAndFormat(returnType);
                Map propertieInfoMap = Maps.newHashMap();
                propertieInfoMap.put("type", strings[0]);
                if (strings.length == 2) {
                    propertieInfoMap.put("format", strings[1]);
                }

                propertiesMap.put(filed, propertieInfoMap);
            }


            sdi.setProperties(propertiesMap);

            this.swagger.addDefinistion(sdi.getName(), sdi.toMap());
        }


    }

    private boolean isGetMethod(Method method) {
        if (method.getParameterCount() != 0) {
            return false;
        }
        if ("getClass".equals(method.getName())) {
            return false;
        }
        if (method.getName().startsWith("get") && method.getName().length() >= 4) {
            return true;
        }
        return false;
    }

    private String[] getSwaggerTypeAndFormat(Class type) {
        if (type == String.class) {
            return new String[]{"string"};
        }


        // mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
        if (type == Integer.class || type == int.class) {
            return new String[]{"integer", "int32"};
        }

        // mysql type: bigint
        if (type == Long.class || type == long.class) {
            return new String[]{"integer", "int64"};
        }


        // mysql type: real, double
//        if (type == Double.class || type == double.class) {
//            return Double.parseDouble(s);
//        }
//
//        // mysql type: float
//        if (type == Float.class || type == float.class) {
//            return Float.parseFloat(s);
//        }


        // mysql type: decimal, numeric
        if (type == Date.class || type == java.sql.Date.class) {
            return new String[]{"string", "date-time"};
        }

//        // mysql type: unsigned bigint
//        if (type == java.math.BigInteger.class) {
//            return new java.math.BigInteger(s);
//        }
//
//        // mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob. I have not finished the test.
//        if (type == byte[].class) {
//            return s.getBytes();
//        }

        return new String[]{"string"};

    }


    public Swagger getSwagger() {
        return swagger;
    }
}
