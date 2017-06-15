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
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.codegen.GenDatasourceBuilder;

import java.util.List;

public class JbootModelGenerator {


    public static void main(String[] args) {

        String modelPackage = "io.jboot.codegen.test";

        String dbHost = "127.0.0.1";
        String dbName = "jbootdemo";
        String dbUser = "root";
        String dbPassword = "";

        run(modelPackage, dbHost, dbName, dbUser, dbPassword);

    }


    public static void run(String modelPackage, String dbHost, String dbName, String dbUser, String dbPassword) {
        new JbootModelGenerator(modelPackage, dbHost, dbName, dbUser, dbPassword).doGenerate();
    }


    private final String basePackage;
    private final String dbHost;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;


    public JbootModelGenerator(String basePackage, String dbHost, String dbName,
                               String dbUser, String dbPassword) {

        this.basePackage = basePackage;
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }


    public void doGenerate() {

        String modelPackage = basePackage + ".model";
        String baseModelPackage = basePackage + ".model.base";

        String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);


        List<TableMeta> tableMetaList = new MetaBuilder(new GenDatasourceBuilder(dbHost,dbName,dbUser,dbPassword).build()).build();

        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new JbootModelnfoGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        System.out.println("generate finished !!!");

    }



}
