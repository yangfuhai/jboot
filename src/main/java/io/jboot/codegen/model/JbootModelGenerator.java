/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.codegen.model;

import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import io.jboot.codegen.CodeGenHelpler;

public class JbootModelGenerator extends ModelGenerator {


    private MetaBuilder metaBuilder;

    public JbootModelGenerator(String modelPackageName,
                               String baseModelPackageName, String modelOutputDir) {
        super(modelPackageName, baseModelPackageName, modelOutputDir);

        this.template = "/io/jboot/codegen/model/model_template.tp";
        this.metaBuilder = CodeGenHelpler.createMetaBuilder();

    }

    public void generate() {
        super.generate(metaBuilder.build());
    }

    /**
     * 设置需要被移除的表名前缀
     * 例如表名  "tb_account"，移除前缀 "tb_" 后变为 "account"
     */
    public JbootModelGenerator setRemovedTableNamePrefixes(String... prefixes) {
        metaBuilder.setRemovedTableNamePrefixes(prefixes);
        return this;
    }

    public JbootModelGenerator addExcludedTable(String... excludedTables) {
        metaBuilder.addExcludedTable(excludedTables);
        return this;
    }


}
