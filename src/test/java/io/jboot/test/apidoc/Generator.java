package io.jboot.test.apidoc;

import io.jboot.apidoc.ApiDocConfig;
import io.jboot.apidoc.ApiDocManager;

public class Generator {

    public static void main(String[] args) {

//        JbootApplication.setBootArg("jboot.datasource.type","mysql");
//        JbootApplication.setBootArg("jboot.datasource.url","jdbc:mysql://127.0.0.1:3306/ketang8?useUnicode=true&characterEncoding=utf-8&useSSL=false");
//        JbootApplication.setBootArg("jboot.datasource.user","root");
//        JbootApplication.setBootArg("jboot.datasource.password","123456");
//
//        ApiJsonGenerator.genRemarksJson();
//        ApiJsonGenerator.genMockJson();


        ApiDocConfig config = new ApiDocConfig();
        config.setAllInOneEnable(true);
//        config.setBasePath();

        ApiDocManager.me().genDocs(config);
    }
}
