# Json 配置

## 接收 Json

### @JsonBody
在 Controller 中，我们可以通过给参数添加 @JsonBody 注解来把客户端传入的 Json 数据，转换为我们需要的参数。

#### @JsonBody 接收 Bean

我们定义的 Bean 如下：

```java
public class MyBean {    
    private String id;    
    private int age;    
    private BigInteger amount;    
    
    //getter setter
}
```

示例1：假设前端传入的 Json 数据内容如下：


```json
{  
    "id":"abc",         
    "age":17,         
    "amount":123
 }
```

在 Controller 中，我们只需要编写如下内容就可以正常接收：

```java
public void bean(@JsonBody() MyBean bean) {    
    System.out.println("bean--->" + bean);    
    renderText("ok");
}
```


示例2：假设我们接收的 Bean 在 Json 实体里，例如：
```json
{  "aaa":{     
        "bbb":{         
            "id":"abc",         
            "age":17,         
            "amount":123
      }
   }
 }
```

 在 Controller 中，我们只需要编写如下内容就可以正常接收：

```java
public void bean(@JsonBody("aaa.bbb") MyBean bean) {    
    System.out.println("bean--->" + bean);    
    renderText("ok");
}
```

#### @JsonBody 接收 Map

 接收 Map 和 Bean 是一样的，只是参数不同。

示例1：假设前端传入的 Json 数据内容如下：


```json
{  
    "id":"abc",         
    "age":17,         
    "amount":123
 }
```

在 Controller 中，我们只需要编写如下内容就可以正常接收：

```java
public void bean(@JsonBody() Map bean) {    
    System.out.println("bean--->" + bean);    
    renderText("ok");
}
```


示例2：假设我们接收的 Map 在 Json 实体里，例如：
```json
{  "aaa":{     
        "bbb":{         
            "id":"abc",         
            "age":17,         
            "amount":123
      }
   }
 }
```
 Controller 代码如下：
```java
public void bean(@JsonBody("aaa.bbb") Map bean) {    
    System.out.println("bean--->" + bean);    
    renderText("ok");
}
```

#### @JsonBody 接收 集合（List、Set、Queue、Vector、Stack、Deque 和 数组）

示例1：前端传入的内容如下

```json
[1,2,3]
```

如下的方法，都可以正常接收数据：

```java
//通过 int[] 数组来接收
public void method1(@JsonBody() int[] beans) {        
     System.out.println("beans--->" + beans);        
     renderText("ok");
} 

 //通过 String[] 数组来接收
public void method2(@JsonBody() String[] beans) {        
    ystem.out.println("beans--->" + beans);        
    enderText("ok");
} 

//通过 List 来接收
public void method3(@JsonBody() List beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
} 
 
//通过 Set 来接收
public void method4(@JsonBody() Set beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
} 
 
//通过 List 指定泛型 Integer 来接收
public void method5(@JsonBody() List<Integer> beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
} 
 
//通过 Set 指定泛型 Integer 来接收
public void method6(@JsonBody() Set<Integer> beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
} 
 
 //通过 List 指定泛型 String 来接收
public void method7(@JsonBody() List<String> beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
} 
 
 //通过 Set 指定泛型 String 来接收
public void method8(@JsonBody() Set<String> beans) {        
    System.out.println("beans--->" + beans);        
    renderText("ok");
}
```

当然，我们可以把 List 或者 Set 修改为  Queue、Vector、Stack、Deque 等其他数据类型。

如果我们要接收的数组包括在 Json 实体里，例如：

```json
{  
"aaa":{      
        "bbb":[1,2,3]
      }
}
```

只需要在 @JsonBody 添加对应的前缀即可，比如：

```java
public void method1(@JsonBody("aaa.bbb") int[] beans) {        
     System.out.println("beans--->" + beans);        
     renderText("ok");
}
```

 示例2，接收 Bean 数组：

比如前端传入的是：

```json
{    
"aaa":{        
    "bbb":[
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            },
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            }
        ]
    }
}

```

Controller 的代码如下：

```java
public void list(@JsonBody("aaa.bbb") List<MyBean> list) {        
      System.out.println("list--->" + list);        
      renderText("ok");
  }
```

或者

```java
public void set(@JsonBody("aaa.bbb") Set<MyBean beans) {        
    System.out.println("array--->" + beans);        
    renderText("ok");
}
```
或者

```java
public void array(@JsonBody("aaa.bbb") MyBean[] beans) {        
    System.out.println("array--->" + beans);        
    renderText("ok");
}
```

### 通过 @JsonBody 接收基本数据

假设客户端（前端）传入的数据内容如下，我们想获取 `id` 的值。

```json
{  "aaa":{     
        "bbb":{         
            "id":"abc",         
            "age":17,         
            "amount":123
      }
   }
 }
```

Controller 代码如下：

```java
 public void id(@JsonBody("aaa.bbb.id") String id) {        
      System.out.println("id--->" + id);        
      renderText("ok");
  }
```

或者我们接收 `age` 参数，内容 Controller 代码如下：

```java
public void age(@JsonBody("aaa.bbb.age") int age) {        
      System.out.println("age--->" + age);        
      renderText("ok");
  }
```
 此处要注意，因为 `age` 在方法中定义的是 `int` 类型，当前端没有传入任何值得时候，`age` 得到的值为： `0`。所以，如果我们想接收基本数据类型的值，建议定义为其封装类型，比如 int 定义为 Integer，long 定位为 Long 等。


### @JsonBody 高级特性：自动拆装

示例1：假设前端传入的内容如下：

```json
{    
"aaa":{        
    "bbb":[
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            },
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            }
        ]
    }
}
```

我们想接收到所有 `id` 值，并自动转换为一个 `String[]` 数组、或者 `List<String> `等类型，接收代码如下：

```java
public void array(@JsonBody("aaa.bbb[id]") String[] ids) {        
    System.out.println("array--->" + ids);        
    renderText("ok");
}
```

或者 
```java
public void array(@JsonBody("aaa.bbb[id]") List<String> ids) {        
    System.out.println("array--->" + ids);        
    renderText("ok");
}
```

示例2，如果我们想获取数组里的某个值，比如前端传入的数据内容如下：

```json
{    
"aaa":{        
    "bbb":[
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            },
            {                
            "id":"abc",                
            "age":17,                
            "amount":123
            }
        ]
    }
}
```

我们想获取 第一个 `age` 的值，接收数据内容如下：

```java
public void age(@JsonBody("aaa.bbb[0].age") long age) {
    renderText("intInArray--->" + age);
}
```

示例3：
假设前度传入的 Json 内容如下：

```json
{
    "aaa":{
        "bbb":[
            {
                "attr1":"abc",
                "beans":[
                    {
                        "id":"abc",
                        "age":17,
                        "amount":123
                    },
                    {
                        "id":"abc",
                        "age":17,
                        "amount":123
                    }
                ]
            },
            {
                "attr2":"abc"
            }
        ]
    }
}
```
我们想获取 beans 的值，Controller 内容如下：

```java
public void array(@JsonBody("aaa.bbb[0].beans") MyBean[] beans) {
    System.out.println("array--->" + JsonKit.toJson(beans));
    renderText("ok");
}
```

如果我们想获取 `beans` 下的所有 `id` 值，Controller 内容如下：

```java
public void array(@JsonBody("aaa.bbb[0].beans[id]") String[] ids) {
    System.out.println("array--->" + JsonKit.toJson(ids));
    renderText("ok");
}
```

### 直接通过 Controller 接收 Json

在 Controller 中，我们如果不通过 `@JsonBody` 接收 Json 数据，也是没问题的。

例如：前端传入的 Json 内容如下：

```json
{
    "aaa":{
        "bbb":[
            {
                "attr1":"abc",
                "beans":[
                    {
                        "id":"abc",
                        "age":17,
                        "amount":123
                    },
                    {
                        "id":"abc",
                        "age":17,
                        "amount":123
                    }
                ]
            },
            {
                "attr2":"abc"
            }
        ]
    }
}
```

我们想获取 beans 的值，Controller 内容如下：

```java
public void array() {
    MyBean[] beans = getRawObject(MyBean[].class,"aaa.bbb[0].beans");
    System.out.println("array--->" + JsonKit.toJson(beans));
    renderText("ok");
}
```

此处要注意，如果我们想获取一个泛型 `List<MyBean>`，需要使用如下方法：

```java
public void array() {
    List<MyBean> beans = getRawObject(new TypeDef<List<MyBean>>(){},"aaa.bbb[0].beans");
    System.out.println("array--->" + JsonKit.toJson(beans));
    renderText("ok");
}
```

或者

```java
public void array() {
    Set<MyBean> beans = getRawObject(new TypeDef<Set<MyBean>>(){},"aaa.bbb[0].beans");
    System.out.println("array--->" + JsonKit.toJson(beans));
    renderText("ok");
}
```

如果我们想获取 `beans` 下的所有 `id` 值，Controller 内容如下：


```java
public void array(@JsonBody() {
    String[] ids = getRawObject(String[].class,"aaa.bbb[0].beans[id]");
    System.out.println("array--->" + JsonKit.toJson(ids));
    renderText("ok");
}
```

或者 
```java
public void array() {
    List<Strint> ids = getRawObject(new TypeDef<List<String>>(){},"aaa.bbb[0].beans[id]");
    System.out.println("array--->" + JsonKit.toJson(ids));
    renderText("ok");
}
```

或者

```java
public void array() {
    Set<Strint> ids = getRawObject(new TypeDef<Set<String>>(){},"aaa.bbb[0].beans[id]");
    System.out.println("array--->" + JsonKit.toJson(ids));
    renderText("ok");
}
```

## 输出 Json


### jboot v3.5.1（包括 3.5.1） 之后的配置

```
#是否启用大写转换，比如 user_age 自动转为为 UserAge，只对 Model 生效，默认值为 true
jboot.json.camelCaseJsonStyleEnable = true

#是否转换任何地方，比如 map.put("my_field") 默认输出为 myField，默认值为 false
jboot.json.camelCaseToLowerCaseAnyway = false

#是否跳过 null 值输出，map.put("key",null)，则 key 值不输，默认值为 true
jboot.json.skipNullValueField = true

#配置输出的时间格式，默认值为 yyyy-MM-dd HH:mm:ss
jboot.json.timestampPattern = "yyyy-MM-dd HH:mm:ss"

# 是否跳过 model 的 attr，只用 bean 的 getter 来渲染
jboot.json.skipModelAttrs = false

# 是否值跳过 bean 的 getter，只用 model 的 attr 渲染
jboot.json.skipBeanGetters =false
```

### jboot v3.5.1 之前的配置

```
#是否启用大写转换，比如 user_age 自动转为为 UserAge
jboot.web.camelCaseJsonStyleEnable = true

#是否转换任何地方，比如 map.put("my_field") 默认输出为 myField
jboot.web.camelCaseToLowerCaseAnyway = false

#配置输出的时间格式
jboot.web.jsonTimestampPattern
```

### 通过 Java 代码配置

更多的配置可以通过 JFinalJsonKit 工具类来进行配置。

### 其他

- 1、通过 @JsonIgnore 注解可以忽略某个字段，比如

```java
public class User{
    
    @JsonIgnore
    public String getPassword(){
        return Supper.getPassword();
    }
}
```

以上代码可以忽略 password 的输出。

- 2、支持 FastJson 的 JsonField 的配置

```java
public class User {

    @JSONField(name = "sex")
    public String getSexString(){
         return "男";
    }
}
```

输出字段又 `sexString` 重新修改为 `sex`。

