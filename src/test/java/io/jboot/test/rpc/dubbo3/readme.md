整体上来说，jboot 升级 dubbo2 到 dubbo3 是不需要修改其他任何代码的

理论上只需要添加两个配置就可以了

- 1、生产端增加配置:
jboot.rpc.dubbo.registry.registerMode=instance


- 2、消费端配置:
jboot.rpc.application.service-discovery.migration=FORCE_APPLICATION
