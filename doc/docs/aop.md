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

当一个接口有多个实现类，或者当在系统中存在多个实例对象，比如有两份 Cache 对象，一份可能是 Redis Server1，一份可能是 Redis Server2，或者有两份数据源 DataSource 等，在这种情况下，我们注入的时候就需要确定注入那个实例。

这时候，我们就需要用到 `@Bean(name= "myName")` 去给不同的子类去添加注释。

例如：


```java
@RequestMapping("/helloworld")
public class MyController extends Controller{

    @Inject
    @Bean(name="userServieImpl1") //注入名称为 userServieImpl1 的实例
    private UserService userService1;

    @Inject
    @Bean(name="userServieImpl2") //注入名称为 userServieImpl2 的实例
    private UserService userService2;

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


UserServiceImpl1：

```java
@Bean(name="userServiceImpl1")
public class UserServiceImpl1 implements UserService{
    public List<User> findAll(){
        //do sth
    }
}
```


UserServiceImpl2：

```java
@Bean(name="userServiceImpl2")
public class UserServiceImpl2 implements UserService{
    public List<User> findAll(){
        //do sth
    }
}
```

这种情况，只是针对一个接口有多个实现类的情况，那么，如果是一个接口只有一个实现类，但是有多个实例，如何进行注入呢？

参考 @Configuration 。


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
```

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
```

## @Configuration


在 Jboot 中的 `@Configuration` 和 Spring 体系的 `@Configuration` 功能类似。


我们可以在一个普通类中添加注解 `@Configuration` , 然后在其方法通过 `@Bean` 去对方法进行添加注解。

例如：

```java
@Configuration
public class AppConfiguration {

    @Bean(name = "myCommentServiceFromConfiguration")
    public CommentService myCommentService1(){
        CommentService commentService = new CommentServiceImpl();
        return commentService;
    }

    @Bean
    public CommentService myCommentService2(){
        CommentService commentService = new CommentServiceImpl();
        return commentService;
    }
}

```

这样，在一个 Jboot 应用中，就会存在两份 `CommentService` 他们的名称分别为：myCommentServiceFromConfiguration 和 myCommentService2（当只用了注解 @Bean 但是未添加 name 参数时，name 的值为方法的名称）

这样，我们就可以在 Controller 里，通过 `@Inject` 配合 `@Bean(name = ... )` 进行注入，例如：


```java
@RequestMapping("/aopcache")
public class AopCacheController extends JbootController {

    @Inject
    @Bean(name="myCommentService2")
    private CommentService commentService;

    @Inject
    @Bean(name = "myCommentServiceFromConfiguration")
    private CommentService myCommentService;


    public void index() {
        System.out.println("commentService:"+commentService);
        System.out.println("myCommentService:"+myCommentService);
    }
}
```


## @ConfigValue

在 AOP 注入中，可能很多时候我们需要注入的只是一个配置内容，而非一个对象实例，此时，我们就可以使用注解 `@ConfigValue`，例如：

```java
@RequestMapping("/aop")
public class AopController extends JbootController {

    @ConfigValue("undertow.host")
    private String host;

    @ConfigValue("undertow.port")
    private int port;

    @ConfigValue(value = "undertow.xxx")
    private int xxx;
}   
```

此时，配置文件 jboot.properties (包括分布式配置中心) 里配置的 undertow.host 的值自动赋值给 host 属性。其他属性同理。


## @StaticConstruct

静态的构造方法。

在某些类中，这个类的创建方式并不是通过 new 的方式进行创建的，或者可能构造函数是私有的，或者这个类可能是一个单例示例的类，比如：

```java
public class JbootManager {

    private static JbootManager me = new JbootManager();

    public static JbootManager me() {
        return me;
    }

    private JbootManager(){
        //do sth
    }
}
```

我们在其他类注入 JbootManager 的时候，并不希望通过 new JbootManager() 的方式进行创建注入，还是希望通过其方法 `me()` 进行获取。

此时，我们就可以给 JbootManager 类添加 `@StaticConstruct` 注解，例如：

```java
@StaticConstruct
public class JbootManager {

    private static JbootManager me = new JbootManager();

    public static JbootManager me() {
        return me;
    }

    private JbootManager(){
        //do sth
    }
}
```

但如果 JbootManager 有多个返回自己对象的静态方法，我们就可以使用  `@StaticConstruct` 的 value 参数来指定。

例如：

```java
@StaticConstruct("me")
public class JbootManager {

    private static JbootManager me = new JbootManager();

    public static JbootManager me() {
        return me;
    }

    public static JbootManager create(){
        return new JbootManager()
    }

    private JbootManager(){
        //do sth
    }
}
```