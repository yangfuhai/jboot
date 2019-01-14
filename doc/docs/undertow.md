# Undertow

## 目录
- Undertow基本配置
- 其他扩展

## Undertow基本配置

由于 Jboot 是依赖 `Jfinal-Undertow` 进行开发的，因此 `Jfinal-Undertow` 的所有配置 Jboot 都会支持，`Jfinal-Undertow` 的配置文档在：https://www.jfinal.com/doc/1-4

## 其他扩展

### 扩展1：配置位置
在 Jboot 应用中，除了可以在 resource 目录下的 `undertow.txt` 文件进行配置以外，也可以在 `jboot.properties` 文件里配置。

同时可以通过启动参数 和 环境变量等进行配置，Undertow 启动的时候读取配置内容的优先顺序是：

`启动参数` > `JVM属性` > `环境变量` > `jboot.properties` > `undertow.txt`

所以，假设同时在 `jboot.porperties` 和 `undertow.txt` 都配置了 `undertow.port` ，那么 `undertow.txt` 的配置将不会生效。（因为会被 `jboot.porperties` 覆盖）。

关于配置问题，更多请移步到：[配置文件](./config.md)。

### 扩展2：Undertow 随机端口号
在分布式等情况下，应用启动的目的，可能只是为了暴露 `RPC` 服务，本身应用是不提供 WEB 功能，此时 Undertow 的端口号就显得无关重要了。

在这种情况下，需要把 Undertow 的端口配置为随机端口，配置内容如下：

```
undertow.port = * 
```
