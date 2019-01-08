# MVC


## 目录

- 描述
- Controller : 控制器
- Action ：请求的基本单位
- Interceptor ： 拦截器
- FixedInterceptor ：永久拦截器
- Handler ： 处理器
- Render ：渲染器
- Session 
- Cookie
- Jwt ： Json Web Token
- FlashMessage
- Validate ： 验证器
- 安全


## 描述
由于 Jboot 是基于 JFinal 进行二次开发的，因此 Jboot 的 MVC 相关功能是来至于 JFinal 提供的功能。

JFinal 的相关文档： [https://www.jfinal.com/doc/3-1](https://www.jfinal.com/doc/3-1)


## Controller ：控制器

 Controller 是 JFinal 的核心类之一，是 MVC 设计模式中的控制器。基于 Jboot 开发的控制器需要继承 Controller。Controller 也是定义 Action 方法的地点，一个 Controller 可以包含多个 Action 。

 另外，JbootController 扩展了 JFinal 的 Controller 类，增加了 Jwt、FlashMessage 和 其他一些实用的方法。

 建议基于 Jboot 开发的应用，都继承至 JbootController。（备注：只是建议、而非必须。）


 ## Action

在 Controller 之中定义的 `public` 方法称为 Action。Action 是请求的最小单位。Action 方法必须在 Controller 中定义，且必须是 `public` 可见性。

例如：

```java
public class HelloController extends Controller {
    public void index() {
       renderText("此方法是一个action");
    }
    public String test() {
       return "index.html";
    }
}
```
以上代码中定义了两个Action，分别是 ：`HelloController.index()`、`HelloController.test()`。

