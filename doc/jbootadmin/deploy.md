# JbootAdmin 部署文档


JbootAdmin 部署过程主要分为以下几个步骤：
- 第一步：编译
- 第二步：上传到服务器
- 第三步：启动

## 第一步：编译

编译的目的是为了让我们的源码（包含 java、js、css 图片等）打包成一个可以被执行的 jar 文件，有了 jar 文件后，我们便可以通过 java 命令 java -jar 进行启动。

在 JbootAdmin 中，我们需要通过 Maven 进行编译的，因此，在编译之前需要在您的电脑里，安装好 maven ，配置好 Maven 的环境变量。而 Maven 是使用 Java 开发的，因此在您的电脑安装好 Java 也是必须的。

当安装好 Maven 后，我们需要通过命令窗口（shell） 方式进入项目所在根目录，然后执行如下代码：

```
mvn clean package
```

稍等片刻，会在 `starter-cms/target` 目录下，生成文件：starter-cms-1.0.0-jar-with-dependencies.jar。


## 第二步：上传到服务器

上传的工具有很多，比如 xshell 等，关于工具的使用请自行参考相关工具的文档，如果您的电脑是 linux 或者 mac 电脑，可以通过如下命令上传。

```
scp 本地文件目录 root@ip:/服务器目录
```

如下的命令是将本地文件 starter-cms-1.0.0-jar-with-dependencies.jar 上传到服务器 192.168.1.100 的 /data/wwww 目录里。

```
scp /your/path/to/starter-cms-1.0.0-jar-with-dependencies.jar root@192.168.1.100:/data/wwww
```

## 第三步：启动

通过 ssh 登录到服务器后，进入到 `starter-cms-1.0.0-jar-with-dependencies.jar` 文件所在目录，执行命令

java -jar starter-cms-1.0.0-jar-with-dependencies.jar 就可以启动项目，但是这种启动的方式是 "前台" 模式，当我们推出命令窗口后，项目也会自动停止了，因此，我们需要通过 nohup 将其后台启动。


命令如下：

```
nohup java -jar ./starter-cms-1.0.0-jar-with-dependencies.jar >./log_cms.log 2>&1 & 
```

同时项目的相关错误日志会输出到 ./log_cms.log 这个文件里。

为了更加方便我们启动项目，我们也可以在 `starter-cms-1.0.0-jar-with-dependencies.jar` 目录下写一个 shell 脚本，用来启动或者关闭我们的项目，例如：

```
#!/bin/bash 
 
COMMAND="$1"
 
if [[ "$COMMAND" != "start" ]] && [[ "$COMMAND" != "stop" ]] && [[ "$COMMAND" != "restart" ]]; then
        echo "Usage: $0 start | stop | restart"
        exit 0
fi
 
 
# 生成 class path 值
 
function start()
{
    # 运行为后台进程，并且将信息输出到 log_cms.log 文件
    nohup java -jar ./starter-cms-1.0.0-jar-with-dependencies.jar --jboot.app.mode=product >./log_cms.log 2>&1 & 
}
 
function stop()
{
    kill `pgrep -f starter-cms` 2>/dev/null
}
 
if [[ "$COMMAND" == "start" ]]; then
        start
elif [[ "$COMMAND" == "stop" ]]; then
    stop
else
    stop
    start
fi
```

假设这个文件名称为 `cms_deploy.sh` ，我们可以执行如下命令对项目进行控制

- 启动项目 `./cms_deploy.sh start`
- 停止项目 `./cms_deploy.sh stop`
- 重启项目 `./cms_deploy.sh restart`


## 注意事项：

### 1、配置问题

当我们把项目打包成一个 jar 文件后，所有的配置内容都存放在 jar 文件里的 jboot.porperites 里了，无法对其进行修改，但是如果一定要修改其配置，可以把这个配置添加到启动参数里，例如 

```
java -jar ./starter-cms-1.0.0-jar-with-dependencies.jar --jboot.app.mode=product
```

等同于在 jboot.properties 文件里添加了 `jboot.app.mode=product` 的配置，而且会覆盖 jboot.properties 原本的配置。其他参数同理。

另一个方案是在项目里有两个 properties 文件，分别是 jboot.properties 和 jboot-product.properties 文件，当我们启动应用的时候，如果当前的 app 模式是 product，那么系统将会优先读取
`jboot-product.properties` 文件里的内容，只有读取不到的时候，才会去读取 `jboot.propertie` 的内容。更多关于配置问题，请参考：http://jbootprojects.gitee.io/docs/docs/config.html


### 2、安全问题

一般情况下，我们不会把数据库（或者 redis等）的明文密码直接配置在 `jboot.properties` 文件里，配置在 `jboot.properties` 里的密码一般都是加密的，关于配置内容加密的解决方案，请参考： 
http://jbootprojects.gitee.io/docs/docs/config.html#%E9%85%8D%E7%BD%AE%E5%86%85%E5%AE%B9%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86 
