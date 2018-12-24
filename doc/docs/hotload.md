# 热加载

在 Jboot 开发模式下，默认开启热加载功能，若要关闭热加载功能，可以添加配置 `undertow.devMode = false` , 或者通过 `jboot.app.mode = product` 配置修改当前应用为生产环境。

开始热加载的时候，可能会出现 `LinkpageError` 等异常，这个原因是由于用户自定义的 Class 没有被负责热加载的 `HotSwapClassLoader` 接管，需要添加如下配置：

```
jboot.app.hotSwapClassPrefix = xxx1.com, xxx2.com
```