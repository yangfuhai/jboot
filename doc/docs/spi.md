# SPI 扩展

## 目录

- 描述
- Jboot SPI 模块


## 描述

SPI 的全名为 : Service Provider Interface。

**SPI 具体约定**

当服务的提供者，提供了服务接口的一种实现之后，在 jar 包的`META-INF/services/` 目录里同时创建一个以 **服务接口** 命名的文件。该文件里就是实现该服务接口的具体实现类。而 Jboot 装配这个模块的时候，就能通过该 jar 包 `META-INF/services/` 里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。

## Jboot SPI 模块
在jboot中，一下模块已经实现了SPI机制。

- Jbootrpc
- JbootHttp
- JbootCache
- Jbootmq
- JbootSerializer

例如，在 `JbootCache` 中，内置了三种实现方案：`ehcache`、`redis`、`ehredis`。在配置文件中，我看可以通过 `jboot.cache.type = ehcache` 的方式来指定在 Jboot 应用中使用了什么样的缓存方案。

但是，在 Jboot 中，通过SPI机制，我们一样可以扩展出第4、第5甚至更多的缓存方案出来。

扩展步骤如下：

- 1：编写JbootCache的子类
- 2：通过 `@JbootSpi` 注解给刚刚编写的类设置上一个名字，例如：`@JbootSpi("mycache")`
- 3：通过在jboot.properties文件中配置上类型为 mycache，配置代码如下：`jboot.cache.type = mycache`


通过以上三步，我们就可以完成了对 JbootCache 模块的扩展，其他模块类似。