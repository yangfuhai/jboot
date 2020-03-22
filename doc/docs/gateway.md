# 网关

## 目录

- 概述
- path路由
- host路由
- query路由
- 其他


## 概述

Jboot 已经内置基础了网关，网关功能目前暂时只能通过在 jboot.properties 文件进行配置。

如下是一个正常的 gateway 配置。

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathEquals = /path
jboot.gateway.pathContains = /path
jboot.gateway.pathStartsWith = /path
jboot.gateway.pathEndswith = /path

jboot.gateway.hostEquals = xxx.com
jboot.gateway.hostContains = xxx.com
jboot.gateway.hostStartsWith = xxx.com
jboot.gateway.hostEndswith = xxx.com

jboot.gateway.queryEquals = aa:bb,cc:dd
jboot.gateway.queryContains = aa,bb
```

## Path 路由

Path 路由一般是最常用的路由之一，是根据域名之后的路径进行路由的，Jboot 对 Path 路由提供了 4 中方式：

**1、pathEquals**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathEquals = /user
```

当用户访问 www.xxx.com/user 的时候，自动路由到 `http://youdomain:8080/user`，但是当用户请求 `www.xxx.com/user/other` 的时候不会进行路由。

**2、pathContains**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathContains = /user
```

当 path 中，只要存在 `/user` 就会匹配到该路由，比如 `www.xxx.com/user/other` 或者 `www.xxx.com/other/user/xxx` 都会匹配到。


**3、pathStartsWith**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathStartsWith = /user
```

当 path 中，只要以 `/user` 开头就会匹配到该路由，比如 `www.xxx.com/user/other` ，但是 `www.xxx.com/other/user/xxx` 不会匹配到，因为它是以 `/other` 开头的。

**4、pathEndswith**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathStartsWith = /user
```

当 path 中，只要以 `/user` 结束就会匹配到该路由，比如 `www.xxx.com/other/user` ，但是 `www.xxx.com/user/other` 不会匹配到，因为它是以 `/other` 结束的。


## Host 路由

Host 路由是根据域名进行路由的，Jboot 对 Host 路由提供了 4 中方式：

**1、hostEquals**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostEquals = xxx.xxx.com
```

当用户访问 xxx.xxx.com/user/xx 的时候，自动路由到 `http://youdomain:8080/user/xx`，但是当用户请求 `www.xxx.com/user/xxx` 的时候不会进行路由。

**2、hostContains**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostContains = xxx.xxx.com
```

在 Host 中，只要存在 `xxx.xxx.com` 就会匹配到该路由，比如 `aaa.bbb.xxx.xxx.com/user/other` 会自动路由到 `http://youdomain:8080/user/other`。


**3、hostStartsWith**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostStartsWith = xxx
```

在 Host 中，只要以 `xxx` 开头就会匹配到该路由，比如 `xxx.xxx.com/user/other` ，但是 `www.xxx.com/other/user/xxx` 不会匹配到，因为它的域名（host）是以 `xxx` 开头的。

**4、hostEndswith**

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.hostEndswith = com
```

在 Host 中，只要以 `com` 结束就会匹配到该路由，比如 `www.xxx.com/other/user` ，但是 `www.xxx.org/user/other` 不会匹配到，因为它的域名是以 `org` 结束的。

## Query 路由

根据 get 请求的参数进行路由，注意 post 请求参数不会路由。


**1、queryEquals**
```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.queryEquals = aaa:bbb
```

以上配置中，如果用户访问 `www.xxx.com/controller?aaa=bbb` 会自动路由到 `http://youdomain:8080/controller?aaa=bbb` ，但是如果用户访问 `www.xxx.com/controller?aaa=ccc`不会路由。

**2、queryContains**
```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.queryContains = aaa
```

以上配置中，如果用户访问 `www.xxx.com/controller?aaa=bbb` 会自动路由到 `http://youdomain:8080/controller?aaa=bbb` ，或者用户访问  `www.xxx.com/controller?aaa=ccc` 也会路由到 `http://youdomain:8080/controller?aaa=ccc`，因为 query 都包含了 `aaa=**` 的请求，但是如果用户访问 `www.xxx.com/controller?other=aaa`不会路由。

## 其他
当配置中，如果一个内容存在多个值的时候，需要用英文逗号（,）隔开。

比如:

```
jboot.gateway.name = name
jboot.gateway.uri = http://youdomain:8080
jboot.gateway.enable = true

jboot.gateway.pathContains = /user,/article
```

当 path 中，只要存在 `/user`  或者 存在 `/article` 都会匹配到该路由，比如 `www.xxx.com/user/xxx` 或者 `www.xxx.com/article/xxx` 都会匹配到。

其他同理。
