# Jboot 1.x 升级到 Jboot 2.x 教程

## Class名称修改

- StrUtils -> StrUtil
- FileUtils -> FileUtil
- ClassKits -> ClassUtil
- EncryptCookieUtils -> CookieUtil
- RequestUtils -> RequestUtil
- ArrayUtils -> ArrayUtil
- JbootHttpKit -> HttpUtil
- @JBootRPCService -> 修改为 @RPCInject 和 @RPCBean

## 方法修改

Jboot.me().getXXX -> Jboot.getXXX

## 修改代码生成器生成的代码

- 删除代码生成器生成的 Service类的 join 系列方法和 keep 方法
- 删除代码生成器生成的ServiceImpl的@Singleton注解
- 修改代码生成器生成的 Service类的 Save和SaveOrUpdate方法，修改其返回内容为 ： `Object`

```
public boolean save()
```
修改为

```java
public Object save()
```

同时：
```
public boolean saveOrUpdate()
```
修改为

```java
public Object saveOrUpdate()
```

备注：返回的 `Object` 是 Model 的主键值（id）。

## 其他

其他错误，都是修改了报名，删除错误的 import ，然后重新导入即可。
