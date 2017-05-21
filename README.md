# jboot
jboot is a similar springboot project base on jfinal,we have using in product environment.

# jboot 中文描述
jboot是一个基于jfinal、undertow开发的一个类似springboot的开源框架，我们已经在正式的商业上线项目中使用。内部集成了代码生成，微服务，MQ，RPC，监控等功能，开发者使用及其简单。


#example

maven dependency

```xml
      <dependency>
            <groupId>io.jboot</groupId>
            <artifactId>jboot</artifactId>
            <version>1.0</version>
        </dependency>

```

new a controller

```java
public class MyController extend JbootController{
   public void index(){
        renderText("hello jboot");
   }
}
```

start 

```java
public class MyStarter{
   public static void main(String [] args){
       Jboot.run(args);
   }
}
```