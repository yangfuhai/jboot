# Dubbo2 升级 Dubbo3 文档

dubbo2 升级 dubbo3 ，请先阅读 dubbo3 官网的升级文档：https://dubbo.apache.org/zh/docs/migration/migration-service-discovery/


总体上来说，3.x 是完全兼容 2.x 版本的，因此，理论上只需要添加两个配置即可：

- 1、生产端增加配置:
  jboot.rpc.dubbo.registry.registerMode=instance


- 2、消费端配置:
  jboot.rpc.application.service-discovery.migration=FORCE_APPLICATION


[comment]: <> (为了保持兼容性最简单的升级方式是:)



以下是升级流程：

- 1、 修改工程 pom.xml 中的 dubbo 依赖到最新版本就可以完成升级，其它代码不用动，升级完成后，建议以下参数进行定制调整(2、3步)

  - 2、Provider 端 `jboot.properties` 中
      `jboot.rpc.dubbo.registry.registerMode` 的可选值 interface、instance、all，默认是 all，即接口级地址、应用级地址都注册。

  - instance： 应用级注册；
  - interface：接口级注册；
  - all：双注册（默认值） 即接口和应用都同时注册到注册中心中，双注册不可避免的会带来额外的注册中心存储压力，
  建议修改默认值为 instance 模式，这是 dubbo3 推荐方式。

- 3.Consumer 端 `jboot.properties` 中 `jboot.rpc.application.service-discovery.migration` 
支持的可选值：

    - FORCE_INTERFACE，只消费接口级地址，如无地址则报错，单订阅 2.x 地址
    - APPLICATION_FIRST，智能决策接口级/应用级地址，双订阅
    - FORCE_APPLICATION，只消费应用级地址，如无地址则报错，单订阅 3.x 地址
jboot.rpc.dubbo.application.service-discovery.migration=APPLICATION_FIRST

> 其他注意事项：
> 在 Dubbo3 中，服务提（Provider端）如果采用应用级注册 （instance），注册中心中只会注册应用实例信息，不会注册接口服务信息。接口服务信息存储在元数据中心，在元数据中心存储了实例和接口的映射关系。消费端通过元数据映射关系获取所需要的服务信息。
这套流程是 dubbo3 为了兼容 dubbo2 自动来完成的，官方名称为：服务自省。详情：
https://dubbo.apache.org/zh/docs/examples/service-discovery/#%E4%BB%80%E4%B9%88%E6%98%AF%E6%9C%8D%E5%8A%A1%E8%87%AA%E7%9C%81


## 参考代码

