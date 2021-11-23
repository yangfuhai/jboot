package io.jboot.test.codegen;


import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.codegen.model.JbootBaseModelGenerator;
import io.jboot.codegen.model.JbootModelGenerator;
import io.jboot.codegen.service.JbootServiceImplGenerator;
import io.jboot.codegen.service.JbootServiceInterfaceGenerator;

public class GenTester {

    public static void main(String[] args) {

        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");

        String modelPackage = "io.jboot.test.codegen.model";
        String baseModelPackage = modelPackage + ".base";

        String modelDir = CodeGenHelpler.getUserDir() + "/src/test/java/" + modelPackage.replace(".", "/");
        String baseModelDir = CodeGenHelpler.getUserDir() + "/src/test/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);


        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).setGenerateRemarks(true).generate();
        new JbootModelGenerator(modelPackage, baseModelPackage, modelDir).generate();


        String servicePackage = "io.jboot.test.codegen.service";
        String serviceImplPackage = "io.jboot.test.codegen.service.provider";

        String serviceOutputDir = CodeGenHelpler.getUserDir() + "/src/test/java/" + servicePackage.replace(".", "/");
        String serviceImplOutputDir = CodeGenHelpler.getUserDir() + "/src/test/java/" + serviceImplPackage.replace(".", "/");


        new JbootServiceInterfaceGenerator(servicePackage, serviceOutputDir, modelPackage).generate();
        new JbootServiceImplGenerator(servicePackage, serviceImplPackage, serviceImplOutputDir, modelPackage).setImplName("provider").generate();

    }
}
