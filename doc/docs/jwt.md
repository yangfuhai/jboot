# JWT

## 目录
- JWT 简介
- JWT 配置
- JWT 使用
- 注意事项

## JWT 简介

JWT 是 Json web token 的简称, 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（RFC 7519).该 token 被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT 的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该 token 也可直接被用于认证，也可被加密。

## JWT 配置

- jboot.web.jwt.httpHeaderName：配置JWT的http头的key，默认为 `Jwt`
- jboot.web.jwt.secret：配置JWT的密钥
- jboot.web.jwt.validityPeriod：配置JWT的过期时间，默认不过期


## JWT 使用

在 `JbootController` 中，新增了如下几个用于操作 JWT 的方法：

- setJwtAttr()：设置 jwt 的 key 和 value
- setJwtMap()：把整个 map的key和value 设置到 jwt
- getJwtAttr()：获取 已经设置进去的 jwt 信息
- getJwtAttrs()：获取 所有已经设置进去的 jwt 信息
- getJwtPara()：获取客户端传进来的 jwt 信息，若 jwt 超时或者不被信任，那么获取到的内容为null

## 注意事项

在服务端通过 `setJwtAttr()` 方法设置 JWT 后，Http 的响应头会添加一个名称为 `Jwt` 的属性（可以通过 `jboot.web.jwt.httpHeaderName` 进行配置）。

此时，客户端（浏览器、小程序、APP等）发现 Http 头有该属性后，需要客户端主动把该值存储起来。APP存储到数据库、浏览和小程序可以存储到localStorage等，当客户端进行 Http 请求的时候，需要在 Http 头添加下属性为 `Jwt`、值为之前存储数据 的请求头。

当客户端正确添加 `Jwt` 的 Http 请求头的时候，服务端可以通过 `getJwtPara()` 方法获取到客户端传入的内容。

**注意：** 接收客户端传入的Jwt值是通过`getJwtPara()`方法，而不是 `getJwtAttr()`。