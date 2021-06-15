# Junit 单元测试

## 目录
- Junit4 单元测试
- Junit5 单元测试
- @TestConfig
- @MockMethod

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
Jboot 提供了 `@MockMethod` 注解，方便对 AOP 管理的类进行 Mock 操作。

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

> `@MockMethod` 的优先级高于 `@TestConfig(autoMockInterface = true)` 的配置。
