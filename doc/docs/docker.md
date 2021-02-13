# Jboot 与 Docker

在 Docker 下部署 Jboot 应用，我们除了通过 jboot.properties 给我们的应用进行配置以外，同时可以通过环境变量来配置，环境变量的优先级高于 jboot.properties 配置文件。

在docker下，通过 docker run 启动 jboot 应用容器时，可以通过 -e 参数来给 jboot 应用进行配置。

例如，我们需要给应用配置数据库信息：

```
docker run 
-e JBOOT_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/jpress3
-e JBOOT_DATASOURCE_USER=root 
-e JBOOT_DATASOURCE_PASSWORD=123456 
jpress:v2.0.8
```

这个启动命令，等同于在 jboot.properties 添加如下的配置

```
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jpress3
jboot.datasource.user=root
jboot.datasource.password=123456
```

同时，假设 jboot.properties 已经有对应配置，docker 启动的 -e 参数会覆盖掉 jboot.properties 的配置。