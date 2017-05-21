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
package io.jboot.codegen.vomodel;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.codegen.GenDatasourceBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JbootVoModeGenerator extends BaseModelGenerator {


    public static void main(String[] args) {

        String modelPackage = "io.jboot.codegen.vomodel.test";

        String dbHost = "127.0.0.1";
        String dbName = "motan";
        String dbUser = "root";
        String dbPassword = "";

        run(modelPackage, dbHost, dbName, dbUser, dbPassword);

    }

    public static void run(String voModelPackage, String dbHost, String dbName, String dbUser, String dbPassword) {
        new JbootVoModeGenerator(voModelPackage, dbHost, dbName, dbUser, dbPassword).doGenerate();
    }

//    private final String basePackage;
    private final String dbHost;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;


    public JbootVoModeGenerator(String basePackage, String dbHost, String dbName,
                                String dbUser, String dbPassword) {
        super(basePackage, PathKit.getWebRootPath() + "/src/main/java/" + basePackage.replace(".", "/"));

//        this.basePackage = basePackage;
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        init();
    }


    public void doGenerate() {


        System.out.println("start generate...");

        List<TableMeta> tableMetaList = new MetaBuilder(new GenDatasourceBuilder(dbHost,dbName,dbUser,dbPassword).build()).build();

        generate(tableMetaList);

        System.out.println("generate finished !!!");

    }



    public void init() {

        this.packageTemplate = "%n"
                + "package %s;%n%n";

        this.classDefineTemplate = "/**%n"
                + " * Auto generated, do not modify this file.%n"
                + " */%n"
                + "@SuppressWarnings(\"serial\")%n"
                + "public class %s extends JbootVoModel {%n%n"


        ;


        this.importTemplate = "import io.jboot.db.model.JbootVoModel;%n%n";

//        this.setterTemplate =
//                "\tpublic void %s(%s %s) {%n" +
//                        "\t\tthis.%s = %s;%n" +
//                        "\t}%n%n";
//
//        this.getterTemplate =
//                "\tpublic %s %s() {%n" +
//                        "\t\treturn %s;%n" +
//                        "\t}%n%n";


    }

    @Override
    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(classDefineTemplate,
                tableMeta.modelName + "Vo"));
    }

//    @Override
//    protected void genBaseModelContent(TableMeta tableMeta) {
//        StringBuilder ret = new StringBuilder();
//        genPackage(ret);
//        genImport(ret);
//        genClassDefine(tableMeta, ret);
////        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
////            ret.append(String.format("\tprivate %s %s;\n", columnMeta.javaType, columnMeta.attrName));
////        }
////        ret.append(String.format("%n%n"));
//        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
//            genSetMethodName(columnMeta, ret);
//            genGetMethodName(columnMeta, ret);
//        }
//        ret.append(String.format("}%n"));
//        tableMeta.baseModelContent = ret.toString();
//    }

//    protected void genSetMethodName(ColumnMeta columnMeta, StringBuilder ret) {
//        String setterMethodName = "set" + StrKit.firstCharToUpperCase(columnMeta.attrName);
//        // 如果 setter 参数名为 java 语言关键字，则添加下划线前缀 "_"
//        String argName = javaKeyword.contains(columnMeta.attrName) ? "_" + columnMeta.attrName : columnMeta.attrName;
//        String template = generateChainSetter ? setterChainTemplate : setterTemplate;
//        String setter = String.format(template, setterMethodName, columnMeta.javaType, argName, argName, argName);
//        ret.append(setter);
//    }
//
//    protected void genGetMethodName(ColumnMeta columnMeta, StringBuilder ret) {
//        String getterMethodName = "get" + StrKit.firstCharToUpperCase(columnMeta.attrName);
//        String argName = javaKeyword.contains(columnMeta.attrName) ? "_" + columnMeta.attrName : columnMeta.attrName;
//        String getter = String.format(getterTemplate, columnMeta.javaType, getterMethodName, argName);
//        ret.append(getter);
//    }

    /**
     * base model 覆盖写入
     */
    protected void writeToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = baseModelOutputDir + File.separator + tableMeta.modelName + "Vo" + ".java";
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(tableMeta.baseModelContent);
        } finally {
            fw.close();
        }
    }
}
