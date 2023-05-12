# 2分钟快速开始

本文档的目的是让您学习完成之后，能对 Jboot 有一个整体的了解，开始基于 Jboot 开发自己的应用程序。

本文档假设您已经具备了如下基本技能：

- Java 编程语言的使用
- Maven 依赖管理的使用
- Java 开发工具的使用

## 目录

- 通过开发工具创建 Maven 项目
- 在 `pom.xml` 上添加 Jboot 依赖
- 编写 hello world 代码
- 运行并查看效果



## 通过开发工具创建 Maven 项目

   略


## 在 `pom.xml` 上添加 Jboot 依赖

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>4.1.0</version>
</dependency>
```

## 编写 hello world 代码


```java
@RequestMapping("/")
public class HelloworldController extends JbootController {

    public void index(){
        renderText("hello world");
    }

    public static void main(String[] args){
        JbootApplication.run(args);
    }
}
```
> 注意：真正项目开发中，建议不要把 `main` 方法写在 `Controller` 里，否则会导致这个 Controller 的热加载出现问题。

## 启动 `main()` 方法，并在浏览器查看

浏览器输入：`http://127.0.0.1:8080`，此时，能看到浏览器显示 `hello world` 的文字内容。




