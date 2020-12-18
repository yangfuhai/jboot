# 代码生成器

Jboot 内置了一个简易的代码生成器，可以用来生成model层和Service层的基础代码，在生成代码之前，请先配置jboot.properties关于数据库相关的配置信息，Jboot 代码生成器会通过该配置去链接数据库。

```
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jbootdemo
jboot.datasource.user=root
jboot.datasource.password=your_password
```

编写带有 main 方法的可以运行的工具类，调用 JbootBaseModelGenerator、JbootModelGenerator、JbootServiceInterfaceGenerator、JbootServiceImplGenerator 的 generate() 方法即可。


例如：

```java
public class GenTester {

    public static void main(String[] args) {

        //配置数据量的连接信息，可以通过 JbootApplication.setBootArg 来配置
        //也可以在 jboot.properties 里配置
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");

        
        String modelPackage = "io.jboot.test.codegen.model"; //生成的Model的包名
        String baseModelPackage = "io.jboot.test.codegen.modelbase"; //生成的BaseModel的包名

        //Model存放的路径，一般情况下是 /src/main/java 下，如下是放在 test 目录下
        String modelDir = PathKit.getWebRootPath() + "/src/test/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/test/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);

        //开始生成 Model 和 BaseModel 的代码
        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).setGenerateRemarks(true).generate();
        new JbootModelGenerator(modelPackage, baseModelPackage, modelDir).generate();


        String servicePackage = "io.jboot.test.codegen.service"; // service 层的接口包名
        String serviceImplPackage = "io.jboot.test.codegen.service.provider"; // service 层的接口实现类包名


        //设置 service 层代码的存放目录
        String serviceOutputDir = PathKit.getWebRootPath() + "/src/test/java/" + servicePackage.replace(".", "/");
        String serviceImplOutputDir = PathKit.getWebRootPath() + "/src/test/java/" + serviceImplPackage.replace(".", "/");


        //开始生成代码
        new JbootServiceInterfaceGenerator(servicePackage, serviceOutputDir, modelPackage).generate();
        new JbootServiceImplGenerator(servicePackage, serviceImplPackage, serviceImplOutputDir, modelPackage).setImplName("provider").generate();

    }
}
````
