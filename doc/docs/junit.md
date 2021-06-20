# Junit 单元测试

## 目录
- Junit4 单元测试
- Junit5 单元测试
- @TestConfig
- @MockMethod
- @MockClass

## Junit 单元测试简介

单元测试（unit testing），是指对软件中的最小可测试单元进行检查和验证。

JUnit 是一个 Java 编程语言的单元测试框架。JUnit 在测试驱动的开发方面有很重要的发展，是起源于 JUnit 的一个统称为 xUnit 的单元测试框架之一。


目前市面上主要是使用 Junit4 和 Junit5 对 Java 程序进行单元测试。


## Junit4 单元测试

1、第一步，添加 junit4 的 maven 依赖

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

2、第二步，编写单元测试代码

```java
@RunWith(JbootRunner.class)
public class MyAppTester {

    private static MockMvc mvc = new MockMvc();

    @Inject
    private MyService myService;

    @Test
    public void test_url_aaa() {
        MockMvcResult mvcResult = mvc.get("/aaa");

        mvcResult.printResult()
                .assertThat(result -> Assert.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_url_bbb() {
        MockMvcResult mvcResult = mvc.get("/bbb");

        mvcResult.printResult()
                .assertThat(result -> Assert.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_my_service() {
        Ret ret = myService.doSomeThing();
        Assert.assertNotNull(ret);
        //.....
    }
}
```
> 注意：Junit4 测试类必须添加 `@RunWith(JbootRunner.class)` 配置


## Junit5 单元测试

1、第一步，添加 junit4 的 maven 依赖

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.7.2</version>
    <scope>test</scope>
</dependency>
```

2、第二步，编写单元测试代码

```java
@ExtendWith(JbootExtension.class)
public class MyAppTester {

    private static MockMvc mvc = new MockMvc();

    @Inject
    private MyService myService;

    @Test
    public void test_url_aaa() {
        MockMvcResult mvcResult = mvc.get("/aaa");

        mvcResult.printResult()
                .assertThat(result -> Assertions.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_url_bbb() {
        MockMvcResult mvcResult = mvc.get("/bbb");

        mvcResult.printResult()
                .assertThat(result -> Assertions.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_my_service() {
        Ret ret = myService.doSomeThing();
        Assertions.assertNotNull(ret);
        //.....
    }
}
```
> 注意：Junit5 测试类必须添加 `@ExtendWith(JbootExtension.class)` 配置


## @TestConfig

在测试的过程中，Jboot 默认的 webRootPath 是 `target/classes/webapp` 目录，而 classPath 的目录是 `target/classes`
目录。

如果我们需要修改此目录，则需要在测试类中添加 @TestConfig 注解，对 webRootPath 或 classPath 进行配置。

例如：

```java
@RunWith(JbootRunner.class)
@TestConfig(webRootPath = "your-path",classPath = "your-path")
public class MyAppTester {

    private static MockMvc mvc = new MockMvc();
    

    @Test
    public void test_url_aaa() {
        MockMvcResult mvcResult = mvc.get("/aaa");

        //your code ...
    }


}
```

`@TestConfig(webRootPath = "your-path",classPath = "your-path")` 里的配置路径，可以是绝对路径或相对路径，
若是相对路径，则是相对 `target/test-classes` 目录的路径。

## @MockMethod

Jboot 提供了 `@MockMethod` 注解，方便对 AOP 管理的类里的方法（method）进行 Mock 操作。

例如：
```java
@RunWith(JbootRunner.class)
@TestConfig(autoMockInterface = true)
public class OptionApiControllerTest {

    private static final MockMvc mvc = new MockMvc();

    @Test
    public void query() {
        mvc.get("/api/option/query?key=myKey").printResult();
    }


    @MockMethod(targetClass = JPressCoreInitializer.class)
    public void onHandlerConfig(JfinalHandlers handlers) {
        handlers.add(new JPressHandler());
    }
    

    @MockMethod(targetClass = UtmService.class)
    public void doRecord(Utm utm){
        System.out.println(">>>>>>>>>doRecord: " + utm);
    }
    

    @MockMethod(targetClass = WebInitializer.class,targetMethod = "onEngineConfig")
    public void mock_on_engine_config(Engine engine){
        System.out.println(">>>>>>>>>onEngineConfig: " + engine);
    }
}
```

在以上的代码中，有几个关键的地方
- `@TestConfig(autoMockInterface = true)`，表示当我们测试 `query()` 方法的时候，可能会遇到一些注入进来的接口，但是可能没有实现类（或者说实现类在别的 Maven Module，并没有依赖进来），但是保证不出错。
当调用接口方法时，等同于什么都不做，若有返回值，则返回 `null`。
- `@MockMethod(targetClass = JPressCoreInitializer.class)` 表示复写 `JPressCoreInitializer` 类的 `onHandlerConfig` 方法。
- `@MockMethod(targetClass = UtmService.class)` 表示复写 `UtmService` 类的 `doRecord` 方法。
- `@MockMethod(targetClass = WebInitializer.class,targetMethod = "onEngineConfig")` 表示复写 `WebInitializer` 类的 `onEngineConfig` 方法。

> 注意：
> 
> 1、在以上代码中，`mock_on_engine_config` 方法的参数必须和 `WebInitializer.onEngineConfig` 里的参数一样，
> 或者 `mock_on_engine_config` 可以多出一个 `targetClass` 的对象参数，例如：mock_on_engine_config(WebInitializer webInitializer,Engine engine)
> 
> 2、`@MockMethod` 的优先级高于 `@TestConfig(autoMockInterface = true)` 的配置。


## @MockClass

Jboot 提供了 `@MockClass` 注解，方便对 AOP 管理的类（Class）进行 Mock 操作。

例如，如下的代码是请求了 `/api/article/detail` 这个 API 接口。

```java
public class ArticleApiControllerTest{
    
    static final MockMvc mvc = new MockMvc();
    
    @Test
    public void detail() {
        mvc.get("/api/article/detail?id=1").printResult();
    }
}
```

假设这个 API 接口里的 `Controller` 通过 `ArticleService` 进行进步一查询数据。除了我们可以通过 `@MockMethod` 对 `ArticleService` 的方法进行 Mock，
也编写一个类，实现 `ArticleService` 接口，并通过 `@MockClass` 添加在实现的类上。

```java
@MockClass
public class ArticleServiceMock implements ArticleService {

    @Override
    public Article findById(Object id) {
        Article article = new Article();
        article.setId((Long) id);
        article.setStatus(Article.STATUS_NORMAL);
        return article;
    }

}
```

此时，我们在测试的时候，当 `Controller` 调用了 `ArticleService.findById(id)` 就会自动调用了 `ArticleServiceMock` 里的 `findById` 方法。

相对 `@MockMethod` 而言，`@MockClass` 有几个好处：

- 1、`@MockClass` 注解的类是去实现某个接口的，ide 等工具会自动帮我们生成方法，不易出错。
- 2、当 Mock 很多个方法的时候，可以写到这个类里，使得测试代码更加简洁
- 3、在有多个测试类的时候，多个测试类可以共用相同的 Mock 代码，减少代码量


> 注意：如果 `@MockClass` 和 `@MockMethod` 同时对一个方法进行 Mock，`@MockMethod` 的优先级高于 `@MockClass`。