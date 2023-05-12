# 快速开始

购买 JbootAdmin 拿到源码后，需要进入如下步骤：

- 1）创建数据库
- 2）将源码导入 idea 或者 eclipse 等开发工具
- 3）编译 JbootAdmin
- 4）运行 JbootAdmin
- 5）通过代码生成器，生成自己的业务模块
- 6）定义后台菜单组
- 7）编写 Controller 代码，并生成菜单

## 创建数据库

JbootAdmin 源码内置了一个叫 JbootAdmin.sql 的脚本，创建好数据库后，将该脚本导入即可。但需要注意的是，数据库一定要选择编码 utf8mb4 。

## 将源码导入开发工具

代码目录结构如下

```
.
├── pom.xml
├── core-codegen （代码生成器）
│   ├── pom.xml
│   ├── src
│   
├── core-commons
│   ├── pom.xml
│   ├── readme.md
│   ├── src
│   
├── core-devops （DevOps相关）
│   ├── core-devops-model
│   ├── core-devops-provider
│   ├── core-devops-service
│   ├── core-devops-web-admin
│   ├── core-devops-web-api
│   ├── pom.xml
│   └── readme.md
│   
├── core-framework （JbootAdmin Framework 核心代码）
│   ├── core-framework-model
│   ├── core-framework-provider
│   ├── core-framework-service
│   ├── core-framework-web
│   ├── pom.xml
│   └── readme.md
│   
├── core-web （JbootAdmin Web Controller 相关代码）
│   ├── pom.xml
│   ├── readme.md
│   ├── src
│   
├── core-wechat （微信相关代码）
│   ├── core-wechat-model
│   ├── core-wechat-provider
│   ├── core-wechat-service
│   ├── core-wechat-web
│   └── pom.xml
│   
├── module-cms （ CMS 模块示例）
│   ├── module-cms-commons
│   ├── module-cms-model
│   ├── module-cms-provider
│   ├── module-cms-service
│   ├── module-cms-web
│   └── pom.xml
│   
├── starter-cms （CMS 启动模块）
│   ├── jboot.bat
│   ├── jboot.sh
│   ├── package.xml
│   ├── pom.xml
│   ├── src
│   
├── starter-tomcat  （Tomcat War 包的打包示例）
│   ├── pom.xml
│   ├── src
│   
```

## 编译 JbootAdmin

编译 JbootAdmin 的主要目的，是为了把 Framework 模块的资源文件，包括 html、js 等全部复制打包到"自己" 的模块里来，我们假设 `module-cms` 是自己的模块，那么打包的目的就是为了在 `starter-cms` 里生成可以执行的程序。

我们需要在 JbootAdmin 的根目录执行如下的 Maven 命令进行编译：

```
mvn clean package
```

命令执行的过程中，Maven 可能需要去下载相关依赖的 jar 包，命令执行完毕后，会在 `starter-cms` 的 `target` 目录下生成 `starter-cms-1.0.0.zip` 文件，复制该文件到任何地方解压缩，通过其内置的 jboot.sh 脚本，执行 `./jboot.sh start` 即可启动项目。


## 运行 JbootAdmin

编译完成 JbootAdmin 后，我们以下的 Java 类，然后执行其 `main()` 方法即可。 

```
starter-cms/src/main/io.jboot.cms.admin.CmsStarter
```

控制台输入如下内容，我们通过浏览器访问 `http://0.0.0.0:8003` 既可以显示系统登录信息，登录账号和密码请联系 海哥 获取。(注意:Windows 系统可能需要访问  `http://127.0.0.1:8003` )
```

  ____  ____    ___    ___   ______ 
 |    ||    \  /   \  /   \ |      |
 |__  ||  o  )|     ||     ||      |
 __|  ||     ||  O  ||  O  ||_|  |_|
/  |  ||  O  ||     ||     |  |  |  
\  `  ||     ||     ||     |  |  |  
 \____||_____| \___/  \___/   |__|  
                                    

JbootApplication { name='jboot', mode='dev', version='4.1.0', config='io.jboot.core.JbootCoreConfig' }
Classpath : /Users/michael/work/git/JbootAdmin/starter-cms/target/classes/
Starting JFinal 4.9.01 -> http://0.0.0.0:8003
Info: jfinal-undertow 2.1, undertow 2.0.30.Final, jvm 1.8.0_261

Starting Complete in 2.3 seconds. Welcome To The JFinal World (^_^)

JbootResourceLoader started, Watched resource path name : webapp
```

## 创建自己的业务模块

在 JbootAdmin 中，创建自己的业务模块有 2 中方式：

- 1）在后台创建项目，然后对项目进行表配置
- 2）在 `core-codegen` 模块下，创建自己的代码生成器，代码生成器带有可以执行的 `main()` 方法

我们建议使用第 2 种方式，当每次表结构发生变化的时候，我们只需要执行以下 `main()` 方法就可以了，第一种方式还需要登录到 JbootAdmin 后台去执行相关操作。

例如：CMS 代码生成器的 代码如下：

```java
public class CmsModuleCodeGenerator {

    public static void main(String[] args) {

        String dbTables = "module_cms_article," +
                "module_cms_article_category," +
                "module_cms_article_comment" ;

        String optionsTables = "";
        String layerTables = "";
        String layerOptionsTables = "";

        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootadmin");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");

        String modelPackage = "io.jboot.cms.model";
        String baseModelPackage = modelPackage + ".base";
        String moduleName = "module-cms";

        String modelDir = PathKit.getWebRootPath() + "/../"+moduleName+"/"+moduleName+"-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/../"+moduleName+"/"+moduleName+"-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        Set<String> genTableNames = StrUtil.splitToSet(dbTables, ",");
        MetaBuilder metaBuilder = CodeGenHelpler.createMetaBuilder();
        metaBuilder.setGenerateRemarks(true);
        List<TableMeta> tableMetas = metaBuilder.build();
        tableMetas.removeIf(tableMeta -> genTableNames != null && !genTableNames.contains(tableMeta.name.toLowerCase()));


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetas);


        String servicePackage = "io.jboot.cms.service";
        String providerPackage = "io.jboot.cms.provider";
        String servicePath = PathKit.getWebRootPath() + "/../"+moduleName+"/"+moduleName+"-service/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = PathKit.getWebRootPath() + "/../"+moduleName+"/"+moduleName+"-provider/src/main/java/" + providerPackage.replace(".", "/");


        new ServiceGenerator(servicePackage, modelPackage, servicePath).generate(tableMetas);
        new ProviderGenerator(providerPackage,servicePackage, modelPackage, providerPath).generate(tableMetas);

        //optionsTables
        Set<String> optionsTableNames = StrUtil.splitToSet(optionsTables, ",");
        if (optionsTableNames != null && optionsTableNames.size() > 0) {
            new BaseOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,optionsTableNames));
        }

        //layerTables
        Set<String> layerTableNames = StrUtil.splitToSet(layerTables, ",");
        if (layerTableNames != null && layerTableNames.size() > 0) {
            new BaseLayerModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,layerTableNames));
        }

        //layerOptionsTables
        Set<String> layerOptionsTableNames = StrUtil.splitToSet(layerOptionsTables, ",");
        if (layerOptionsTableNames != null && layerOptionsTableNames.size() > 0) {
            new BaseLayerOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,layerOptionsTableNames));
        }
    }


    private  static List<TableMeta> copyTableMetasByNames(List<TableMeta> tableMetas,Set<String> names){
        List<TableMeta> retList = new ArrayList<>();
        tableMetas.forEach(tableMeta -> {
            if (names.contains(tableMeta.name.toLowerCase())){
                retList.add(tableMeta);
            }
        });
        return retList;
    }

}

```

其他 Module 需要复制一份这个代码，然后修改掉 表明、包名、模块名称即可。


## 定义后台菜单组

我们通过 CmsModuleCodeGenerator 只是生成了 Model/Service/Provider 的代码，并不会生成 Controller 和 html，以及后台菜单，因此，我们需要来定义一个菜单组，我们登陆的时候，才能看到 CMS 模块的菜单，其他模块通用需要定义一个模块自己的菜单组。

如果定义后台菜单呢？代码如下：

```java
public class CmsMenus implements MenuBuilder {

    public static final String ARTICLES = "articles";
    public static final String ARTICLE_CATEGORY = "article-category";


    static List<MenuBean> cmsMenus = new ArrayList<>();

    static {
        cmsMenus.add(new MenuBean(ARTICLES, "文章管理", "fa-cart-plus", MenuTypes.MODULE, 11));
        cmsMenus.add(new MenuBean(ARTICLE_CATEGORY, "文章分类", "fa-crown", MenuTypes.MODULE, 22));
    }


    @Override
    public List<MenuBean> buildMenus() {
        return cmsMenus;
    }

}
```

- 1、编写一个类，实现 MenuBuilder 接口
- 2、复写 buildMenus 方法，并返回一个 `List<MenuBean>`

需要注意的是，如果 MenuBean 有配置了 pid 属性，那说明其实一个 “菜单”，如果没有 pid，说明其实一个 “菜单组”。


## 编写 Controller 代码，并生成菜单

Controller 代码如下

```java
@RequestMapping("/cms/article")
public class ArticleController extends BaseAdminController {

    private CmsArticleService articleService;

    @MenuDef(text = "文章列表", pid = CmsMenus.ARTICLES, sortNo = 1)
    public void list(){
        render("");
    }

}
```

- 1）通过 `@RequestMapping("/cms/article")` 来配置 Controller 的映射路径
- 2）通过 `@MenuDef(text = "文章列表", pid = CmsMenus.ARTICLES, sortNo = 1)` 来定义菜单名称，及其归属在哪个 “菜单组” 下。

完成 Controller 代码后，我们需要进入后台，并点击 ` “开发管理” > “菜单构建”` 就可以显示出我们在 `ArticleController.list()` 里定义的菜单了。

