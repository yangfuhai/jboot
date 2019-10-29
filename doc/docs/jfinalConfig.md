# 如何在Jboot中添加自己的 JFianlConfig

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

我们可以写自己的 Config 继承 JFinalConfig 然后对 JFinal 进行一些列的配置，那么，在 Jboot 中如何来对 JFinal 进行配置呢？

- 第一步：编写一个类继承 JbootAppListenerBase ，编写的类的名称可以自定义（Jboot 的 ClassScanner 会自动扫描到），一个应用在可以存在多个继承至 JbootAppListenerBase 的类
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
    public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
        //FixedInterceptor 类似 Interceptor，
        // 但是 FixedInterceptor 不会被注解 @Clear 清除
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