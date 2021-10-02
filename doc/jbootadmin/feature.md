# JbootAdmin 功能介绍

[[TOC]]

## 基础功能


### 账户管理

![](./features/account.png)

### 部门管理

![](./features/dept.jpeg)

### 职位管理

![](./features/station.jpeg)

### 角色管理

![](./features/role.jpeg)


### 角色的权限分配
对角色的权限进行分配、包括菜单的权限、功能的权限、逻辑权限（根据业务进行人为定义的权限）、敏感数据权限（根据业务进行人为定义的、涉及数据敏感的权限）

![](./features/role_permission.jpeg)

### 参数配置

![](./features/sys_option.jpeg)

### 行政区划

![](./features/sys_area.jpeg)

### 数据字典
JbootAdmin 中的数据字典不同于其他传统意义的数据字典。JbootAdmin 的数据字典可以生成枚举代码 `Enum` ,通过 `Enum` 又可以方便在在 Java 代码和模板中使用。

比如，在后台创建的枚举，可以直接生成如下代码：

```java
@JFinalSharedEnum
public enum PayType {
   
    ALIPAY(1, "支付宝"),
    WECHAT(10, "微信支付"),

    public int value;
    public String text;

    PayType(int value, String text) {
        this.value = value;
        this.text = text;
    }

   
    public static String text(Integer value) {
        if (value != null) {
            for (AccountType type : values()) {
                if (type.value == value) {
                    return type.text;
                }
            }
        }
        return null;
    }


    public static boolean isAlipay(Integer value) {
        return value != null && ALIPAY.value == value;
    }

}
```

而以上代码又可以方便在 Java 或者 模板中使用，例如：

```html
<html>
<body>
   #(PayType.ALIPAY.text)   <br/>
   #(PayType.text(1))   <br/>
   #(PayType.isAlipay(1))   <br/>
</body>
</html>
```

![](./features/sys_dict.jpeg)

![](./features/sys_dict_edit.jpeg)

### 微信公众号对接

支持多个微信公众号，支持菜单配置、根据关键字自动回复、默认回复配置 等等

![](./features/wechat_account.jpeg)

自动回复
![](./features/wechat_reply.jpeg)

通过关键字自动回复...

![](./features/wechat_keyword.jpeg)

![](./features/wechat_keyword_config.jpeg)


## 特色功能（独创）

### 特色功能1：同一套代码支持 Tab 模式和独立页面模式
JbootAdmin 同一套代码，后台支持 Tab 模式，也支持独立页面模式，同时 Tab 模式和独立页面模式支持用户自主切换，也支持后台配置为固定，不允许用户切换。 如下图所示是 Tab 模式：

![](./images/jbootadmin-demo.jpg)

### 特色功能2：免手动维护的权限列表
在一般的系统中，需要我们一边开发，一边手动定义系统有哪些权限，但是在 JbootAdmin 中，所有的权限都是免手动维护的，我们可以通过后台，一键自动生成权限列表，存储到数据库里去。这样避免了繁杂的人为手动维护，也大大减少了出错的可能性。

![](./features/build_permission.jpeg)

### 特色功能3：免手动维护的系统菜单

原理同免维护的权限列表。后台一键构建左边菜单的功能。

![](./features/build_memu.jpeg)


## API文档自动生成

Jboot API 功能自动根据代码、自动生成文档、Debug 页面、对数据进行 Mock 等功能。

### API 代码


![](./features/apidoc_code.png)

### API 生成文档

![](./features/apidoc_info.jpeg)

### API Debug

通过 Debug 功能，我们可以方便的对 API 进行调试。

![](./features/apidoc_debug.jpeg)

### API Mock

通过 API Mock 功能，我们可以模拟 API 数据，在前后端分离的场景下，我们可以使用此功能先给前端团队正常调用数据，等我们完成 API 开发再删除 Mock 数据。

![](./features/apidoc_mock.jpeg)

## 强大代码生成功能

在使用 JbootAdmin 的代码生成器之前，我们可以先创建项目，然后对该项目进行数据配置。

### 项目列表
![](./features/dev_project.jpeg)

### 创建项目，配置数据源
![](./features/dev_project_edit.jpeg)

### 根据数据表，生成代码
![](./features/dev_codgen.jpeg)

### 根据某个表，生成 Model、Service、Provider、Controller、Html 等代码
![](./features/dev_codgen_gen.jpeg)

### 配置某个表对应的 Controller 映射等
![](./features/dev_codgen_edit.jpeg)

### 对表的字段进行配置等
![](./features/dev_codgen_fields.jpeg)


## 运维功能

JbootAdmin 提供了强大的运维功能。1 是内置的功能，2 是通过适配第三方来进行配置。

### 分布式应用列表

可以查看分布式下的某个应用情况

![](./features/devops_apps.jpeg)

### 分布式监控大盘

可以查看分布式下，每个应用所在的机器硬件情况。

![](./features/devops_d.jpeg)

### 分布式缓存监控

对分布式缓存情况查看，刷新等，支持了 ehcache、redis、caffeine、ehredis 等等。

![](./features/devops_cache.jpeg)

### Sentinel 分布式限流

支持自建 Sentinel 控制，也支持阿里云的 AHAS 进行限流控制。

![](./features/devops_ahas.png)

### 基于 Nacos 门户网关自动发现

![](./features/devops_gateway_nacos1.jpeg)

![](./features/devops_gateway_nacos2.jpeg)

### 基于 Grafana 对 Jboot 的 JVM 进行监控

![](./features/devops_grafana_jboot_jvm.png)

### 更多
 
 基于 Dubbo Admin 对  Jboot RPC 控制等不再截图...

## Demos示例

JbootAdmin 提供了一些 Demos 示例，方便用户对 JbootAdmin 内置的前端组件进行全面的了解。

### 产品列表

支持在产品列表里对产品进行基本的操作

![](./features/demo_product.jpeg)

### 产品编辑

在产品编辑中可以对产品的属性进行配置、支持多规格、多单位等等...

![](./features/demo_product_edit.jpeg)

### 产品库存入库单

![](./features/demo_warehousein.jpeg)


### 查看入库单

![](./features/demo_warehousein_view.jpeg)


### 编辑入库单

![](./features/demo_warehousein_edit1.jpeg)

![](./features/demo_warehousein_edit2.jpeg)



## 课堂8 - 在线教育系统

课堂8 是一个基于 JbootAdmin 开发的在线教育系统。

### 课堂8 - 首页

![](./features/ketang8_index.jpeg)

### 课堂8 - 课程详情

在课程详情中，可以设置课程的标题、简介、营销简介、章节目录，是否免费试看、限时价（秒杀价）、会员价等等....

![](./features/ketang8_course_detail.jpeg)

### 课堂8 - 在线学习

当用户未登录时，需要登录才能观看。
![](./features/ketang8_course_study.jpeg)

用户登录后，可以正常观看视频，观看的过程中，会记录课程的当前进度，用户可以通过用户中心再次进入观看，继续学习。
![](./features/ketang8_course_study2.jpeg)

### 课堂8 - 用户微信注册或登录

其流程是：扫码微信二维码 → 关注我们的公众号 → 自动注册。这个过程，不需要用户填写信息。因此，他的注册成本大大降低。

![](./features/ketang8_login_wechat.jpeg)

### 课堂8 - 用户手机登录

![](./features/ketang8_login_mobile1.jpeg)

![](./features/ketang8_login_mobile2.jpeg)

### 课堂8 - 用户中心在线学习

在用户中心中，可以看到自己的学习时间、每个课程的学习进度等等。

![](./features/ketang8_ucenter_studied.jpeg)


### 课堂8 - 修改个人资料

![](./features/ketang8_ucenter_modify.jpeg)


### 课堂8 - 修改个人头像

修改头像中，设计的技术包含了，上传图片、到分布式附件中心，对分布式附件里的图片进行预览和在线剪辑等等功能...

![](./features/ketang8_ucenter_avatar.jpeg)

### 课堂8 - 在线支付购买课程

在线支付环节，看起来内容少，但工作量和细节是巨大的。

- 在 PC 模式下，必须支持 微信支付 和 支付宝支付的选择。
- 在 微信 里，必须隐藏掉微信支付和支付宝支付的选择方式，只能用微信支付。
- 在 H5 浏览器里（比如 UC 浏览器），能够选择支付方式，并在支付的时候自动唤起（打开）手机里的支付宝和微信的 APP 进行支付。

![](./features/ketang8_buy.jpeg)

![](./features/ketang8_buy1.png)

### 课堂8 - 更多

除此之外，我们还做了很多你看不见的工作：

- 基于阿里云的视频加密播放。
- 前后台分离部署，前后台分别部署在不同的机器里。
- 涉及到的Web安全防护，比如 XSS、CSRF 等等。
-  ......