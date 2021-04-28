# 序列化

序列化 (Serialization) 是将 Java 对象转换为可以存储或传输的状态信息。在很多的场景下，比如缓存、分布式调用 RPC，MQ 等都需要到了序列化。才能把 Java 对象传输到另一个其他系统。

在 Jboot 中，已经内置了多种序列化解决方案。

- fst
- kryo
- fastjson

默认已经使用了 FST，当没有特殊需求的时候，使用默认的 fst 就可以了，但是在某些情况下，比如 redis 缓存已经使用了其他序列化方案进行存储数据了，我们要正确读取其数据，需要设置我们的序列化方案为 redis 已经使用的方案。

此时，我们可以通过如下的配置，来修改掉 redis 的序列化：

```properties
jboot.redis.serializer = xxx
```

其中，xxx 是序列化的名称，倘若 Jboot 中不存在此序列化方案，需要用户自行通过 Jboot SPI 进行扩展，


更多关于 SPI 的查看 [这里](./spi.md) 。
