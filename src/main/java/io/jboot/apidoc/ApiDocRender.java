/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.apidoc;

import com.jfinal.template.Engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApiDocRender {
    ApiDocRender MARKDOWN_RENDER = new ApiDocRender() {
        private Engine engine = new Engine("apidoc");
        private String template = "#(\"#\") #(document.value ??)\n" +
                "\n" +
                "#(document.notes ??)" +
                "#for(operation : document.apiOperations)" +
                "\n" +
                "\n" +
                "\n" +
                "#(\"##\") #(operation.value ??)" +
                "\n" +
                "#if(operation.notes)" +
                "\n\n#(operation.notes ??) \n" +
                "#end" +
                "\n" +
                "#('####') 接口信息：\n" +
                "- 访问路径： `#(operation.actionKey ??)`\n" +
                "- 数据类型： `#(operation.contentType.value ??)`\n" +

                "#if(operation.hasParameter())" +
                "\n#('####') 请求参数：\n" +
                "\n" +
                "| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  \n" +
                "| --- | --- | --- | --- | --- | --- |\n" +
                "#for(parameter : operation.apiParameters)" +
                "| #(parameter.name ??) | #(parameter.value ??) | `#(parameter.dataType ??)` | #(parameter.require ? '是' : '否') | #(parameter.httpMethodsString ??) | #(parameter.notesString ??) |  \n" +
                "#end" +
                "#end" + //参数表格信息

                "\n" +
                "\n" +

                "#if(operation.paraNotes)" +
                "> #(operation.paraNotes ??)" +
                "#end" + //参数配置

                "#if(operation.retType)" +
                "\n" +
                "\n" +
                "#('####') 数据响应：`#(operation.retType ??)`\n\n" +

                "#for(item : operation.retRemarks)" +
                "#(item.key ??)\n\n" +
                "| 字段  | 数据类型 | 描述 |  \n" +
                "| --- | --- | --- | \n" +
                "#for(info : item.value)" +
                "| #(info.field ??) | `#(info.classType ??)` | #(info.remarks ??) |  \n" +
                "#end" +
                "\n\n" +
                "#end" + //end 响应字段表格

                "#if(operation.retMockJson)" +
                "**JSON 示例：**\n" +
                "```json\n" +
                "#(operation.retMockJson ??)\n" +
                "```" +
                "#end" + //end json示例

                "#end" + // end operation
                "\n" +
                "\n" +
                "#end";

        @Override
        public void render(List<ApiDocument> apiDocuments, ApiDocConfig config) {
            try {
                for (ApiDocument document : apiDocuments) {
                    doRender(document, config);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void doRender(ApiDocument document, ApiDocConfig config) throws IOException {
            Map<String, Object> templateParas = new HashMap<>();
            templateParas.put("config", config);
            templateParas.put("document", document);

            File file = new File(config.getBasePathAbsolute(), document.getFilePath() + ".md");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            System.err.println("Jboot generated apidoc -----> " + file.getCanonicalPath());
            engine.getTemplateByString(template).render(templateParas, file);
        }
    };


     void render(List<ApiDocument> apiDocuments, ApiDocConfig config);
}
