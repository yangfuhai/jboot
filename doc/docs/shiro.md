# Shiro

## 目录

- Shiro简介
- Shiro的配置
- Shiro的基本使用
- Shiro的高级使用
  - 自定义 Shiro 错误信息
  - Shiro 与 Jwt 整合
  - Shiro 与 SSO（单点登录） 整合

## Shiro简介

Apache Shiro是一个强大且易用的Java安全框架，执行身份验证、授权、密码学和会话管理。使用 Shiro 的易于理解的 API，您可以快速、轻松地获得任何应用程序，从最小的移动应用程序到最大的网络和企业应用程序。

## Shiro的配置

Jboot 默认情况下并没有依赖 shiro，因此，在在使用 Jboot 的 Shiro 模块之前，需要你添加下 Shiro 的 Maven 依赖。

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.3.2</version>
</dependency>

<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-web</artifactId>
    <version>1.3.2</version>
</dependency>
```
注意：目前暂时不支持 Shiro 1.4.x 版本，晚点会添加支持。


同时，需要在 resources 目录下配置上您的 `shiro.ini` 配置文件，并在 `jboot.porperties` 添加上 `jboot.shiro.ini = shiro.ini` 配置。在 `shiro.ini` 文件里，需要在自行扩展 `realm` 等信息。

## Shiro的使用
Jboot 的 Shiro 模块为您提供了以下12个模板指令，同时支持 Shiro 的5个 Requires 注解功能。方便您使用 Shiro。

**12个Html模板指令**
- shiroAuthenticated：用户已经身份验证通过，Subject.login登录成功
- shiroGuest：游客访问时。 但是，当用户登录成功了就不显示了
- shiroHasAllPermission：拥有全部权限
- shiroHasAllRoles：拥有全部角色
- shiroHasAnyPermission：拥有任何一个权限
- shiroHasAnyRoles：拥有任何一个角色
- shiroHasPermission：有相应权限
- shiroHasRole：有相应角色
- shiroNoAuthenticated：未进行身份验证时，即没有调用Subject.login进行登录。
- shiroNotHasPermission：没有该权限
- shiroNotHasRole：没有该角色
- shiroPrincipal：获取Subject Principal 身份信息

**5个Controller注解**
- RequiresPermissions：需要权限才能访问这个action
- RequiresRoles：需要角色才能访问这个action
- RequiresAuthentication：需要授权才能访问这个action，即：SecurityUtils.getSubject().isAuthenticated()
- RequiresUser：获取到用户信息才能访问这个action，即：SecurityUtils.getSubject().getPrincipal() != null 
- RequiresGuest：和RequiresUser相反


### 12个Html模板指令的使用

shiroAuthenticated的使用

```
#shiroAuthenticated()
  登陆成功：您的用户名是：#(SESSION("username"))
#end
```

shiroGuest的使用

```
#shiroGuest()
  游客您好
#end
```
shiroHasAllPermission的使用

```
#shiroHasAllPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1和permissionName2
#end
```

shiroHasAllRoles的使用

```
#shiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1和role2
#end
```

shiroHasAnyPermission的使用

```
#shiroHasAnyPermission(permissionName1,permissionName2)
  您好，您拥有了权限 permissionName1 或 permissionName2 
#end
```

shiroHasAnyRoles的使用

```
#shiroHasAllRoles(role1, role2)
  您好，您拥有了角色 role1 或 role2
#end
```

shiroHasPermission的使用

```
#shiroHasPermission(permissionName1)
  您好，您拥有了权限 permissionName1 
#end
```

shiroHasRole的使用

```
#shiroHasRole(role1)
  您好，您拥有了角色 role1 
#end
```

shiroNoAuthenticated的使用

```
#shiroNoAuthenticated()
  您好，您还没有登陆
#end
```

shiroNotHasPermission的使用

```
#shiroNotHasPermission(permissionName1)
  您好，您没有权限 permissionName1 
#end
```

shiroNotHasRole的使用

```
#shiroNotHasRole(role1)
  您好，您没有角色role1
#end
```

shiroPrincipal的使用

```
#shiroPrincipal()
  您好，您的登陆信息是：#(principal)
#end
```



### 5个Controller注解的使用

RequiresPermissions的使用

```java
public class MyController extends JbootController{

      @RequiresPermissions("permission1")
      public void index(){

	  }
	  
	  @RequiresPermissions(value={"permission1","permission2"},logical=Logincal.AND)
      public void index1(){

	  }
}
```

RequiresRoles的使用

```java
public class MyController extends JbootController{

      @RequiresRoles("role1")
      public void index(){

	  }
	  
	  @RequiresRoles(value = {"role1","role2"},logical=Logincal.AND)
      public void userctener(){

	  }
}
```

RequiresUser、RequiresGuest、RequiresAuthentication的使用

```java
public class MyController extends JbootController{

      @RequiresUser
      public void userCenter(){

	  }
	  
	  @RequiresGuest
      public void login(){

	  }
	  
	  @RequiresAuthentication
	  public void my(){
	  
	  }
}
```


## Shiro的高级使用
### 自定义 Shiro 错误信息


编写一个类实现 实现接口 io.jboot.component.shiro.JbootShiroInvokeListener，例如：

```java
public class MyshiroListener implements  JbootShiroInvokeListener {


    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);


    @Override
    public void onInvokeBefore(FixedInvocation inv) {
        //do nothing
    }

    @Override
    public void onInvokeAfter(FixedInvocation inv, AuthorizeResult result) {

        //说明该用户授权成功，
        //可以允许访问
        if (result.isOk()) {
            inv.invoke();
            return;
        }

        //shiro授权不成功，返回授权错误码
        int errorCode = result.getErrorCode();
        switch (errorCode) {
            case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                doProcessUnauthenticated(inv.getController());
                break;
            case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                doProcessuUnauthorization(inv.getController());
                break;
            default:
                inv.getController().renderError(404);
        }
    }


    public void doProcessUnauthenticated(Controller controller) {
        // 处理认证失败
    }

    public void doProcessuUnauthorization(Controller controller) {
        // 处理授权失败
    }

};
```
其次在jboot.properties中配置即可

```
jboot.shiro.invokeListener=com.xxx.MyshiroListener
```

### Shiro 与 Jwt 整合

### Shiro 与 SSO 整合