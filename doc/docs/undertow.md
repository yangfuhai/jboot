# Undertow

## 目录
- Undertow基本配置
- 其他扩展

## Undertow基本配置

由于 Jboot 是依赖 `Jfinal-Undertow` 进行开发的，因此 `Jfinal-Undertow` 所有配置都会支持，具体文档在：https://www.jfinal.com/doc/1-4

## 其他扩展

### 扩展1：配置位置
在 Jboot 应用在，除了可以在resource下的 `undertow.txt` 文件进行配置以外，所有配置也可以放到 `jboot.properties` 文件下。

也可以通过启动参数 和 环境变量等进行配置，应用启动的时候读取的优先顺序是：

`启动参数` > `JVM属性` > `环境变量` > `jboot.properties` > `undertow.txt`

关于配置问题，更多请移步到：[配置文件](./config.md)。

### 扩展2：Undertow 随机端口号
在分布式等情况下，应用的启动可能只是为了暴露 `RPC` 服务，本身应用是不提供 WEB 功能的，此时 Undertow 的端口号就显得无关重要了。

在这种情况下，只可能需要把 Undertow 的端口配置为随机端口，配置为随机端口，只需要添加如下配置即可：

```
undertow.port = * 
```
