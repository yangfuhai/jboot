# 面向切面编程

## 目录

- JFinal AOP
- JBoot AOP
- @Inject
- @RPCInject
- @Bean
- @BeanExclude
- @Configuration
- @ConfigValue
- @StaticConstruct

## JFinal AOP

参考文档：https://jfinal.com/doc/4-6

## JBoot AOP

JBoot AOP 在 JFinal AOP 的基础上，新增了我们在分布式下常用的功能，同时借鉴了 Spring AOP 的一些特征，对 JFinal AOP 做了功能的强制，但是又没有 Spring AOP 体系的复杂度。

## @Inject

我们可以通过 @Inject 对任何 Bean 的属性进行注入，例如 Controller

```java
@RequestMapping("/helloworld")
public class MyController extends Controller{

    @Inject
    private UserService userService;

    public void index(){
        renderJson(userService.findAll());
    }
}
```

在以上的例子中，在默认情况下，JFinal AOP 会去实例化一个 UserService 实例，并注入到 MyController 的 userService 中，因此需要注意的是：UserService 必须是一个可以被实例化的类，**不能是**抽象类或者接口（Interface）。

如果说，UserService 是一个接口，它有实现类比如 `UserServiceImpl.class`，JFinal 提供了另一种方案，代码如下：

```java
@RequestMapping("/helloworld")
public class MyController extends Controller{

    @Inject(UserServiceImpl.class)
    private UserService userService;

    public void index(){
        renderJson(userService.findAll());
    }
}
```

## @Bean
在以上的例子中，我们认为 `@Inject(UserServiceImpl.class)` 这可能不是最好的方案，因此，JBoot 提供了可以通过注解 `@Bean` 给 `UserServiceImpl.class` 添加在类上，这样 Jboot 在启动的时候，会自动扫描到 `UserServiceImpl.class` ，并通过 JbootAopFactory 把 UserService 和 UserServiceImpl 添加上关联关系。

代码如下：

Controller：

```java
@RequestMapping("/helloworld")
public class MyController extends Controller{

    @Inject
    private UserService userService;

    public void index(){
        renderJson(userService.findAll());
    }
}
```

UserService：

```java
public interface UserService{
    public List<User> findAll();
}
```


UserServiceImpl：

```java
@Bean
public class UserServiceImpl implements UserService{
    public List<User> findAll(){
        //do sth
    }
}
```

## @BeanExclude

当我们使用 `@Bean` 给某个类添加上注解之后，这个类会做好其实现的所有接口，但是，很多时候我们往往不需要这样做，比如：

```java
@Bean
public class UserServiceImpl implements UserService, 
OtherInterface1,OtherInterface2...{

    public List<User> findAll(){
        //do sth
    }
}

在某些场景下，我们可能只希望 UserServiceImpl 和 UserService 做好映射关系，此时，`@BeanExclude` 就派上用场了。

如下代码排除了 UserServiceImpl 和 OtherInterface1,OtherInterface2 的映射关系。

```java
@Bean
@BeanExclude({OtherInterface1.cass,OtherInterface2.class})
public class UserServiceImpl implements UserService, 
OtherInterface1,OtherInterface2...{

    public List<User> findAll(){
        //do sth
    }
}