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
package io.jboot.codegen.service;

import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;

import java.util.List;

public class JbootServiceGenerator {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");

        String basePackage = "io.jboot.codegen.service.test";
        String modelPackage = "io.jboot.codegen.test.model";
        run(basePackage, modelPackage);

    }

    public static void run(String basePackage, String modelPacket) {
        new JbootServiceGenerator(basePackage, modelPacket).doGenerate(null);
    }

    public static void run(String basePackage, String modelPacket, String excludeTables) {
        new JbootServiceGenerator(basePackage, modelPacket).doGenerate(excludeTables);
    }


    private String basePackage;
    private String modelPacket;

    public JbootServiceGenerator(String basePackage, String modelPacket) {
        this.basePackage = basePackage;
        this.modelPacket = modelPacket;

    }


    public void doGenerate(String excludeTables) {

        System.out.println("start generate...");
        List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
        CodeGenHelpler.excludeTables(tableMetaList, excludeTables);

        new JbootServiceInterfaceGenerator(basePackage, modelPacket).generate(tableMetaList);
        new JbootServiceImplGenerator(basePackage + ".impl", modelPacket).generate(tableMetaList);


        System.out.println("service generate finished !!!");

    }


}
