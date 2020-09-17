# 如何在Jboot中添加自己的 JFinalConfig

[[toc]]

凡是开发过 JFinal 的同学，都知道 JFinalConfig 是 JFinal 的核心配置，详情： https://www.jfinal.com/doc/2-1 ，其内容如下：

```java
public class DemoConfig extends JFinalConfig {
    public void configConstant(Constants me) {}
    public void configRoute(Routes me) {}
    public void configEngine(Engine me) {}
    public void configPlugin(Plugins me) {}
    public void configInterceptor(Interceptors me) {}
    public void configHandler(Handlers me) {}
}
```

默认情况下，我们不需要对 JFinal 进行任何的配置，因为 Jboot 已经对 JFinal 进行了默认的配置，同时，Controller 等的配置完全是通过注解
@RequestMapping 来配置了，数据库也只是在 jboot.properties 里添加就可以。


但是可能在某些特殊情况下，我们对 JFinal 进行自己特殊的配置，如何来做呢？

- 第一步：编写一个类继承 JbootAppListenerBase 
- 第二步：复写对应的方法


JbootAppListenerBase 提供的方法如下：

```java
public class JbootAppListenerBase implements JbootAppListener {


    @Override
    public void onInit() { 
        //会在以下所有方法之前进行优先调用
    }

    @Override
    public void onConstantConfig(Constants constants) { 
        //对应 JFinalConfig 的 configConstant
    }

    @Override
    public void onRouteConfig(Routes routes) {
        //对应 JFinalConfig 的 configRoute
    }

    @Override
    public void onEngineConfig(Engine engine) {
        //对应 JFinalConfig 的 configEngine
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        //对应 JFinalConfig 的 configPlugin
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        //对应 JFinalConfig 的 configInterceptor
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        //对应 JFinalConfig 的 configHandler
    }

    @Override
    public void onStartBefore() {
        // 此方法会在 onStart() 之前调用
    }

    @Override
    public void onStart() {
        //对应 JFinalConfig 的 onStart()
    }

    @Override
    public void onStop() {
        //对应 JFinalConfig 的 onStop()
    }

}
```

编写的继承 JbootAppListenerBase 的类名可以是任意名称，Jboot 的 ClassScanner 会自动扫描到并在 App 启动的时候自动执行，同时 ，一个应用在可以存在多个继承至 JbootAppListenerBase 的类，这样更加方便团队配合和模块化开发，每个团队（或模块）都可以有自己的配置类，不会造成代码冲突。