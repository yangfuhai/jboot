# MQ 消息队列

## 基本使用

Jboot 内置了对MQ消息队列的功能支持，使用MQ需要以下几步步骤。

**第一步：配置jboot.properties文件，内容如下：**

```properties
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

```java
 Jboot.getMq().publish(yourObject, "channel1");
```

需要注意的是，两个服务器的 mq 类型、和服务器信息一定是一致的。消息的接收端的 `jboot.mq.channel` 配置必须包含 "channel1" 才能正常接收数据 。


其他更多关于 rabbitmq、aliyunmq、qpidmq 等的配置，需要查看 [这里](./config.md)。

## Rabbitmq

```properties
#设置 mq 的相关信息
jboot.mq.type=rabbitmq
jboot.mq.channel=channel1


#以下可以不用配置，是默认信息
jboot.mq.rabbitmq.username=guest
jboot.mq.rabbitmq.password=guest
jboot.mq.rabbitmq.host=127.0.0.1
jboot.mq.rabbitmq.port=5672
jboot.mq.rabbitmq.virtualHost=

#非常重要，多个应用如果同时接受同一个 channel 的广播，必须配置此项，而且必须不能相同，否则广播的时候只有一个应用能够接受到
jboot.mq.rabbitmq.broadcastChannelPrefix=app1
```

## Redis-mq

```properties
jboot.mq.type=redis
jboot.mq.channel=channel1,channel2,myChannel
jboot.mq.redis.host=127.0.0.1
jboot.mq.redis.port=6379
jboot.mq.redis.timeout=2000
jboot.mq.redis.password
jboot.mq.redis.database
jboot.mq.redis.clientName
jboot.mq.redis.testOnCreate
jboot.mq.redis.testOnBorrow
jboot.mq.redis.testOnReturn
jboot.mq.redis.testWhileIdle
jboot.mq.redis.minEvictableIdleTimeMillis
jboot.mq.redis.timeBetweenEvictionRunsMillis
jboot.mq.redis.numTestsPerEvictionRun
jboot.mq.redis.maxAttempts
jboot.mq.redis.maxTotal
jboot.mq.redis.maxIdle
jboot.mq.redis.minIdle
jboot.mq.redis.maxWaitMillis
```

## Rocketmq

```properties
jboot.mq.type=rocketmq
jboot.mq.channel=channel1,channel2,myChannel
jboot.mq.rocket.namesrvAddr=127.0.0.1:9876
jboot.mq.rocket.namespace
jboot.mq.rocket.consumerGroup = "jboot_default_consumer_group";
jboot.mq.rocket.consumeMessageBatchMaxSize
jboot.mq.rocket.broadcastChannelPrefix "broadcast-";
jboot.mq.rocket.producerGroup "jboot_default_producer_group";
```

## 多 MQ 实例

若在一个应用在，里面有多个 MQ 实例，假设有两个 redis 实例和两个 Rabbitmq 实例，配置如下：

```properties
# 默认 MQ 及其类型
jboot.mq.type=redis
jboot.mq.channel=channel1,channel2,myChannel

jboot.mq.redis.host=127.0.0.1
jboot.mq.redis.port=6379
jboot.mq.redis.timeout=2000


# 其他另一个 mq1 的类型
jboot.mq.other1.type=redis
jboot.mq.other1.typeName=redis1
jboot.mq.other1.channel=channel1,channel2....


jboot.mq.redis.redis1.host=127.0.0.1
jboot.mq.redis.redis1.port=6379
jboot.mq.redis.redis1.timeout=2000


# 其他另一个 mq2 的类型
jboot.mq.other2.type=rabbitmq
jboot.mq.other2.typeName=rabbitmqaaa
jboot.mq.other2.channel=channel1,channel2....

jboot.mq.rabbitmq.rabbitmqaaa.username=guest
jboot.mq.rabbitmq.rabbitmqaaa.password=guest
jboot.mq.rabbitmq.rabbitmqaaa.host=127.0.0.1
jboot.mq.rabbitmq.rabbitmqaaa.port=5672

# 其他另一个 mq3 的类型
jboot.mq.other3.type=rabbitmq
jboot.mq.other3.typeName=rabbitmqbbb
jboot.mq.other3.channel=channel1,channel2....

jboot.mq.rabbitmq.rabbitmqbbb.username=guest
jboot.mq.rabbitmq.rabbitmqbbb.password=guest
jboot.mq.rabbitmq.rabbitmqbbb.host=127.0.0.1
jboot.mq.rabbitmq.rabbitmqbbb.port=5672


```

