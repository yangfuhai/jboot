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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JbootBaseModelGenerator extends BaseModelGenerator {

    public JbootBaseModelGenerator(String baseModelPackageName,
                                   String baseModelOutputDir) {
        super(baseModelPackageName, baseModelOutputDir);

        this.packageTemplate = "%n"
                + "package %s;%n%n";

        this.classDefineTemplate = "/**%n"
                + " *  Auto generated, do not modify this file.%n"
                + " */%n"
                + "@SuppressWarnings(\"serial\")%n"
                + "public class %s<M extends %s<M>> extends JbootModel<M> implements IBean {%n%n"

                + "\tpublic static final String ACTION_ADD = \"%s:add\";%n"
                + "\tpublic static final String ACTION_DELETE = \"%s:delete\";%n"
                + "\tpublic static final String ACTION_UPDATE = \"%s:update\";%n%n"

                + "\t@Override%n"
                + "\tprotected String addAction() {%n"
                + "\t\treturn ACTION_ADD;%n"
                + "\t}%n%n"

                + "\t@Override%n"
                + "\tprotected String deleteAction() {%n"
                + "\t\treturn ACTION_DELETE;%n"
                + "\t}%n%n"

                + "\t@Override%n"
                + "\tprotected String updateAction() {%n"
                + "\t\treturn ACTION_UPDATE;%n"
                + "\t}%n%n"


        ;


        this.importTemplate = "import io.jboot.db.model.JbootModel;%n"
                + "import com.jfinal.plugin.activerecord.IBean;%n%n";


    }

    @Override
    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(classDefineTemplate, tableMeta.baseModelName,
                tableMeta.baseModelName, StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName), tableMeta.baseModelName,
                tableMeta.baseModelName, tableMeta.baseModelName));
    }


}
