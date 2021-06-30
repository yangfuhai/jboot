package io.jboot.test.apidoc;

import io.jboot.apidoc.ApiDocConfig;
import io.jboot.apidoc.ApiDocManager;

public class Generator {

    public static void main(String[] args) {
        ApiDocConfig config = new ApiDocConfig();
//        config.setBasePath();

        ApiDocManager.me().genDocs(config);
    }
}
