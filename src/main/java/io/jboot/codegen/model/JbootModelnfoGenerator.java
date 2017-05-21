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

import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JbootModelnfoGenerator extends ModelGenerator {

	public JbootModelnfoGenerator(String modelPackageName,
                                  String baseModelPackageName, String modelOutputDir) {
		super(modelPackageName, baseModelPackageName, modelOutputDir);

		this.importTemplate = "import io.jboot.db.annotation.Table;%n"
				+ "import %s.%s;%n%n";

		this.classDefineTemplate =
				"@Table(tableName = \"%s\", primaryKey = \"%s\")%n" +
				"public class %s extends %s<%s> {%n";


		this.daoTemplate = "\tprivate static final long serialVersionUID = 1L;%n%n";

	}

	@Override
	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(classDefineTemplate, tableMeta.name,tableMeta.primaryKey,tableMeta.modelName, tableMeta.baseModelName, tableMeta.modelName));
	}




}
