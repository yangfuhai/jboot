# MQ 消息队列

Jboot 内置了对MQ消息队列的功能支持，使用MQ需要以下几步步骤。

**第一步：配置jboot.properties文件，内容如下：**

```
# 默认为redis (支持: redis,activemq,rabbitmq,hornetq,aliyunmq等 )
jboot.mq.type = redis
jboot.mq.channel = channel1,channel2,channel3
jboot.mq.redis.host = 127.0.0.1
jboot.mq.redis.password =
jboot.mq.redis.database =
```

**第二步：在服务器A中添加一个MQ消息监听器，用于监听 channel1 的消息**

```java 
Jboot.getMq().addMessageListener(new JbootmqMessageListener(){
        @Override
        public void onMessage(String channel, Object obj) {
           System.out.println(obj);
        }
}, "channel1");
```

**第三步：开始监听**

```java
Jboot.getMq().startListening();
```

配置完毕后，我们在其他服务器，就可以通过如下代码发送消息：

```
 Jboot.getMq().publish(yourObject, "channel1");
```

需要注意的是，两个服务器的 mq 类型、和服务器信息一定是一致的。消息的接收端的 `jboot.mq.channel` 配置必须包含 "channel1" 才能正常接收数据 。


其他更多关于 rabbitmq、aliyunmq、qpidmq 等的配置，需要查看 [这里](./config.md)。