# 后台菜单

JbootAdmin 的菜单，不需要进行手动维护，不需要在数据库进行新增、删除或者修改等操作，JbootAdmin 全部是是自动维护的，我们只需要专注于编码即可。

JbootAdmin 的菜单定义有两种方式

- 1）通过实现 MenuBuilder 来定义菜单
- 2）通过注解 @MenuDef 来定义菜单


## 通过实现 MenuBuilder 来定义菜单

```java
public class AdminMenus implements MenuBuilder {

    public static final String ACCOUNT = "account";
    public static final String WECHAT = "wechat";

    static List<MenuBean> frameworkMenus = new ArrayList<>();
    static {
        frameworkMenus.add(new MenuBean(ACCOUNT, "账户管理", "fa-user", MenuTypes.SYSTEM, 111));
        frameworkMenus.add(new MenuBean(WECHAT, "微信平台", "fab fa-weixin", MenuTypes.SYSTEM, 222));
    }

    @Override
    public List<MenuBean> buildMenus() {
        return frameworkMenus;
    }
}
```
- 1） 编写任意类名，实现 `MenuBuilder` 接口
- 2） 复写 `buildMenus()` 方法，返回 `List<MenuBean>`，每个 MenuBean 可以理解为一个菜单，但当这个菜单没有 pid （父级ID）的时候，可以理解其为一个 “菜单组”，给子菜单使用的。


所以，以上的代码，是定义两个菜单组，这个菜单组的 ID 分别是 account 和 wechat 。

## 通过注解 @MenuDef 来定义菜单

```java
@RequestMapping("/wechat")
public class WechatController extends BaseAdminController {

    @MenuDef(text = "微信账户", pid = "wechat", sortNo = 1)
    public void list(){
        render("");
    }

}
```

以上的 `@MenuDef` 定义了一个菜单；

- 菜单名称：微信账户
- 归属菜单组：微信平台 （因为其 pid 值 wechat 是 ”微信平台“ 这个菜单组的 id ）
- 排序序号：1
- URL地址： /wechat/list
