/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.codegen.service;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JbootServiceInterfaceGenerator extends BaseModelGenerator {

    private String modelPacket;
    private String basePackage;

    public JbootServiceInterfaceGenerator(String basePackage, String modelPacket) {
        super(basePackage, PathKit.getWebRootPath() + "/src/main/java/" + basePackage.replace(".", "/"));


        this.modelPacket = modelPacket;
        this.basePackage = basePackage;
        this.template = "io/jboot/codegen/service/service_template.jf";

    }

    @Override
    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate base model ...");
        System.out.println("Base Model Output Dir: " + baseModelOutputDir);

        Engine engine = Engine.create("forService");
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", getterTypeMap);
        engine.addSharedObject("javaKeyword", javaKeyword);

        for (TableMeta tableMeta : tableMetas) {
            genBaseModelContent(tableMeta);
        }
        writeToFile(tableMetas);
    }

    @Override
    protected void genBaseModelContent(TableMeta tableMeta) {
        Kv data = Kv.by("baseModelPackageName", baseModelPackageName);
        data.set("generateChainSetter", generateChainSetter);
        data.set("tableMeta", tableMeta);
        data.set("modelPacket", modelPacket);
        data.set("basePackage", basePackage);

        Engine engine = Engine.use("forService");
        tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
    }


    /**
     * base model 覆盖写入
     */
    @Override
    protected void writeToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = baseModelOutputDir + File.separator + tableMeta.modelName + "Service" + ".java";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }

        FileWriter fw = new FileWriter(target);
        try {
            fw.write(tableMeta.baseModelContent);
        } finally {
            fw.close();
        }
    }


}
