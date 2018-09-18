/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package db;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.codegen.model.JbootBaseModelGenerator;
import io.jboot.codegen.model.JbootModelGenerator;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.codegen
 */
public class dbGen {

    public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");

        String modelPackage = "db.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/src/test/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/test/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);

        List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
        CodeGenHelpler.excludeTables(tableMetaList, null);


        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new JbootModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

    }
}
