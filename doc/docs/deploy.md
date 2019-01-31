# 项目部署

## 目录
- 描述
- 通过 Jar 运行
- 通过 Tomcat 运行

## 描述


## 通过 Jar 运行

Jboot 通过依赖 `JFinal-Undertow` 内置了 Undertow 服务器，可以直接通过 Jar 的方式进行运行，这部分直接参考文档：https://www.jfinal.com/doc/1-3 即可。

## 通过 Tomcat 运行

Jboot 在 Tomcat 运行，在需要在如下的目录下创建 web.xml 文件

```
  src
    ├── main
    │   ├── java
    │   ├── resources
    │   └── webapp
    │       └── WEB-INF
    │           └── web.xml
```

web.xml 的内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
         id="WebApp_ID" version="2.5">

    <filter>
        <filter-name>jfinal</filter-name>
        <filter-class>com.jfinal.core.JFinalFilter</filter-class>
        <init-param>
            <param-name>configClass</param-name>
            <param-value>io.jboot.core.JbootCoreConfig</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>jfinal</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```