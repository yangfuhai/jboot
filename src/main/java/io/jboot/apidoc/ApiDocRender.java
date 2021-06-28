package io.jboot.apidoc;

import com.jfinal.template.Engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ApiDocRender {
    public static ApiDocRender DEFAULT_RENDER = new ApiDocRender() {
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
                "#(operation.notes ??)" +
                "\n" +
                "- 访问路径: `#(operation.actionKey ??)`\n" +
                "- 数据类型: `#(operation.contentType.value ??)`\n" +
                "#if(operation.hasParameter())" +
                "- 参数：\n" +
                "\n" +
                "  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  \n" +
                "  | --- | --- | --- | --- | --- |\n" +
                "#for(parameter : operation.apiParameters)" +
                "  | #(parameter.name ??) | #(parameter.value ??) | #(parameter.require ? '是' : '否') | #(parameter.httpMethodsString ??) | #(parameter.notesString ??) |  \n" +
                "#end" +
                "#end" +
                "\n" +
                "\n" +
                "#if(operation.paraNotes)" +
                "   > #(operation.paraNotes ??)" +
                "#end" +
                "#end";

        @Override
        void render(List<ApiDocument> apiDocuments, ApiDocConfig config) {

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


    abstract void render(List<ApiDocument> apiDocuments, ApiDocConfig config);
}
