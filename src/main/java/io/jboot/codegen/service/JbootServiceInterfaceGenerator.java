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

public class JbootServiceInterfaceGenerator extends BaseModelGenerator {


    String modelPacket;

    public JbootServiceInterfaceGenerator(String basePackage, String modelPacket) {
        super(basePackage, PathKit.getWebRootPath() + "/src/main/java/" + basePackage.replace(".", "/"));

        this.modelPacket = modelPacket;

        this.packageTemplate = "%n"
                + "package %s;%n%n";

        this.classDefineTemplate = "public interface %s  {%n%n" + "\n" +
                "\n" +
                "    /**\n" +
                "     * 根据ID查找model\n" +
                "     *\n" +
                "     * @param id\n" +
                "     * @return\n" +
                "     */\n" +
                "    public %s findById(Object id);\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 查找全部数据\n" +
                "     *\n" +
                "     * @return\n" +
                "     */\n" +
                "    public List<User> findAll();\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 根据ID删除model\n" +
                "     *\n" +
                "     * @param id\n" +
                "     * @return\n" +
                "     */\n" +
                "    public boolean deleteById(Object id);\n" +
                "\n" +
                "    /**\n" +
                "     * 删除\n" +
                "     *\n" +
                "     * @param model\n" +
                "     * @return\n" +
                "     */\n" +
                "    public boolean delete(%s model);\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 保存到数据库\n" +
                "     *\n" +
                "     * @param model\n" +
                "     * @return\n" +
                "     */\n" +
                "    public boolean save(%s model);\n" +
                "\n" +
                "    /**\n" +
                "     * 保存或更新\n" +
                "     *\n" +
                "     * @param model\n" +
                "     * @return\n" +
                "     */\n" +
                "    public boolean saveOrUpdate(%s model);\n" +
                "\n" +
                "    /**\n" +
                "     * 更新 model\n" +
                "     *\n" +
                "     * @param model\n" +
                "     * @return\n" +
                "     */\n" +
                "    public boolean update(%s model);\n";


        this.importTemplate = "";

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


    @Override
    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(classDefineTemplate,
                tableMeta.modelName + "Service", tableMeta.modelName, tableMeta.modelName, tableMeta.modelName, tableMeta.modelName, tableMeta.modelName));
    }


    protected void genImport(StringBuilder ret, TableMeta tableMeta) {
        ret.append(String.format("import %s.%s;%n%n", modelPacket, tableMeta.modelName));
        ret.append("\nimport java.util.List;");
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
