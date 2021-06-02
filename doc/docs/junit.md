# Junit 单元测试

## 目录
- Junit4 单元测试
- Junit5 单元测试

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
    <version>4.12</version>
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
                .assertTrue(result -> result.getHttpCode() == 200);
    }

    @Test
    public void test_url_bbb() {
        MockMvcResult mvcResult = mvc.get("/bbb");

        mvcResult.printResult()
                .assertThat(result -> Assert.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getHttpCode() == 200);
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
    <version>5.4.2</version>
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
                .assertTrue(result -> result.getHttpCode() == 200);
    }

    @Test
    public void test_url_bbb() {
        MockMvcResult mvcResult = mvc.get("/bbb");

        mvcResult.printResult()
                .assertThat(result -> Assertions.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getHttpCode() == 200);
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
