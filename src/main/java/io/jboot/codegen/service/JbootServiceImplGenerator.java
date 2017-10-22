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

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JbootServiceImplGenerator extends BaseModelGenerator {

    String modelPacket;

    public JbootServiceImplGenerator(String basePackage, String modelPacket) {
        super(basePackage, PathKit.getWebRootPath() + "/src/main/java/" + basePackage.replace(".", "/"));

        this.modelPacket = modelPacket;

        this.packageTemplate = "%n"
                + "package %s;%n%n";

        this.classDefineTemplate = "public class %s extends JbootServiceBase<%s> implements %s {%n%n";


        this.importTemplate = "import io.jboot.service.JbootServiceBase;%n%n";
    }

    protected void genBaseModelContent(TableMeta tableMeta) {
        StringBuilder ret = new StringBuilder();
        genPackage(ret);
        genImport(ret, tableMeta);
        genClassDefine(tableMeta, ret);
        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
            genSetMethodName(columnMeta, ret);
            genGetMethodName(columnMeta, ret);
        }
        ret.append(String.format("}%n"));
        tableMeta.baseModelContent = ret.toString();
    }

    protected void genImport(StringBuilder ret, TableMeta tableMeta) {
        ret.append(String.format("import %s.%sService;%n", baseModelPackageName.substring(0, baseModelPackageName.lastIndexOf(".")), tableMeta.modelName));
        ret.append(String.format("import %s.%s;%n", modelPacket, tableMeta.modelName));
        ret.append("import io.jboot.service.JbootServiceBase;\n\n");
    }

    @Override
    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(classDefineTemplate,
                tableMeta.modelName + "ServiceImpl", tableMeta.modelName, tableMeta.modelName + "Service"));
    }

    @Override
    protected void genGetMethodName(ColumnMeta columnMeta, StringBuilder ret) {
//        super.genGetMethodName(columnMeta, ret);
    }

    @Override
    protected void genSetMethodName(ColumnMeta columnMeta, StringBuilder ret) {
//        super.genSetMethodName(columnMeta, ret);
    }

    /**
     * base model 覆盖写入
     */
    protected void writeToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = baseModelOutputDir + File.separator + tableMeta.modelName + "ServiceImpl" + ".java";

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
