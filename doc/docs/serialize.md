# 序列化

在jboot中，很多分布式的调用，需要把 javaBean 序列化后才能进行传输 或者 进行缓存。比如 rpc、mq、cache 等都需要序列化组件。

例如，在 redis 中，我们可以为 redis 组件自定义自己的序列化方式，只需要进入如下配置即可：

```
jboot.redis.serializer = xxxx
```

自定义序列化是通过Jboot的SPI机制来实现的，我们只需要安装 SPI 规范来实现自己的序列化组件，然后通过以上配置即可。


更多关于 SPI 的查看 [这里](.spi.md) 。
