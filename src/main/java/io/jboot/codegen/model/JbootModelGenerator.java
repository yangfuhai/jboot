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
package io.jboot.codegen.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;

import java.util.List;

public class JbootModelGenerator {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");


        String modelPackage = "io.jboot.codegen.test.model";
        run(modelPackage);
    }


    public static void run(String modelPackage) {
        new JbootModelGenerator(modelPackage).doGenerate(null);
    }

    public static void run(String modelPackage, String excludeTables) {
        new JbootModelGenerator(modelPackage).doGenerate(excludeTables);
    }


    private final String basePackage;


    public JbootModelGenerator(String basePackage) {
        this.basePackage = basePackage;
    }


    public void doGenerate(String excludeTables) {

        String modelPackage = basePackage;
        String baseModelPackage = basePackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);

        List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
        CodeGenHelpler.excludeTables(tableMetaList, excludeTables);


        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new JbootModelnfoGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        System.out.println("model generate finished !!!");

    }


}
