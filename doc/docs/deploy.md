# 项目部署

## 目录
- 描述
- 通过 脚本 运行
- 通过 Jar 运行
- 通过 Tomcat 运行

## 描述

本文档提供了 3 种部署方式，对应 Jboot 里的 3 种[打包方式](./build.md)。

## 通过 脚本 运行

在 [打包方式](./build.md) 文档中，我们可以把项目打包成一个 .zip 的压缩包项目，里面带有 jboot.sh （和 jboot.bat） 执行脚本，
只需要我们解压 .zip 压缩文件，通过如下命令就可以对 jboot 项目进行启动和停止。

```shell script
# 启动
./jboot.sh start

# 停止
./jboot.sh stop

# 重启
./jboot.sh restart
```

在 Windows 系统中，通过如下命令执行

```shell script
# 启动
jboot.bat start

# 停止
jboot.bat stop

# 重启
jboot.bat restart
```

## 通过 Jar 运行

在 [打包方式](./build.md) 文档中，我们可以把所有的资源文件（html、css、js、配置文件 等）以及项目的所有依赖打包到一个 jar 包
里去，打包成功后，可以通过如下命令运行。

启动（前台启动，命令窗口不能关闭）
```shell script
java -jar xxx.jar
```
> 当前ssh窗口（命令窗口）被锁定，可按 `CTRL + C` 打断程序运行，或直接关闭窗口，程序退出。

启动（后台启动，命令窗口不能关闭）
```shell script
java -jar xxx.jar &
```
> & 代表在后台运行。当前ssh窗口不被锁定，但是当窗口关闭时，程序中止运行。


启动（后台启动）
```shell script
nohup java -jar xxx.jar &
```
>nohup 意思是不挂断运行命令,当账户退出或终端关闭时,程序仍然运行，当用 nohup 命令执行作业时，缺省情况下该作业的所有输出被重定向到nohup.out的文件中，除非另外指定了输出文件。

启动（后台启动）
```shell script
nohup java -jar xxx.jar>temp.txt &
```
>command >out.file 是将 command 的输出重定向到 out.file 文件，即输出内容不打印到屏幕上，而是输出到 out.file 文件中。
>以上命令，就是把 java 启动的输出，输入到 temp.txt 文件里，而不是在屏幕上。
 
 
另外：可以通过如下命令实时查看日志：

```shell script
tail -f temp.text
```

可通过jobs命令查看后台运行任务
 
```shell script
jobs 
```
 
jobs 命令就会列出所有后台执行的作业，并且每个作业前面都有个编号。如果想将某个作业调回前台控制，只需要 fg + 编号即可。
 
```shell script
fg 33
```

查看程序端口的进程的pid

```shell script
netstat -nlp | grep:8080
``` 




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