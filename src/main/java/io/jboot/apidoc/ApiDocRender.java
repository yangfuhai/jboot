package io.jboot.apidoc;

import com.jfinal.template.Engine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ApiDocRender {
    public static ApiDocRender DEFAULT_RENDER = new ApiDocRender() {
        private Engine engine = new Engine("apidoc");
        private String template = "# #(document.value ??)\n" +
                "\n" +
                "#(document.notes ??)" +
                "\n" +
                "\n" +
                "#for(operation : document.apiOperations)" +
                "## #(operation.value ??)\n" +
                "\n" +
                "- 访问路径: `#(operation.actionKey ??)`\n" +
                "- 数据类型: `#(operation.contentType.value ??)`\n" +
                "#if(operation.hasParameter())" +
                "- 参数：\n" +
                "\n" +
                "  | 名称 | 参数 | 是否必须 | 提交方式 | 描述 |  \n" +
                "  | --- | --- | --- | --- | --- |\n" +
                "#for(parameter : operation.apiParameters)" +
                "  | #(parameter.value ?? | #(parameter.name ??) | #(parameter.require ? '是' : '否') | #(parameter.httpMethodsString) | #(#(parameter.notes ??) |  \n" +
                "#end" +
                "#end" +
                "\n" +
                "> #(operation.notes ??)" +
                "#end";

        @Override
        void render(List<ApiDocument> apiDocuments, ApiDocConfig config) {

//            engine.setBaseTemplatePath(config.getBasePathAbsolute());

            for (ApiDocument document : apiDocuments) {
                Map<String, Object> templateParas = new HashMap<>();
                templateParas.put("config", config);
                templateParas.put("document", document);

                File file = new File(config.getBasePathAbsolute(), document.getFilePath() + ".md");
                engine.getTemplateByString(template).render(templateParas, file);
            }
        }
    };


    abstract void render(List<ApiDocument> apiDocuments, ApiDocConfig config);
}
