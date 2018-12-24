# 事件机制

为了解耦，Jboot 内置了一个简单易用的事件系统，使用事件系统非常简单。

```java

@EventConfig(action = {"event1","event2"})
public class MyEventListener implements JbootEventListener {
    
    public  void onEvent(JbootEvent event){
        Object data = event.getData();
        System.out.println("get data:"+data);
    }
}
```

通过 `@EventConfig` 配置 让 MyEventListener 监听上 `event1` 和`event2` 两个事件。


在项目任何地方发送事件：

```java

Jboot.sendEvent("event1",  object)
```