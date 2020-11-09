# Json 配置

## jboot v3.5.1（包括 3.5.1） 之后的配置

```
#是否启用大写转换，比如 user_age 自动转为为 UserAge，只对 Model 生效，默认值为 true
jboot.json.camelCaseJsonStyleEnable = true

#是否转换任何地方，比如 map.put("my_field") 默认输出为 myField，默认值为 false
jboot.json.camelCaseToLowerCaseAnyway = false

#是否跳过 null 值输出，map.put("key",null)，则 key 值不输，默认值为 true
jboot.json.skipNullValueField = true

#配置输出的时间格式，默认值为 yyyy-MM-dd HH:mm:ss
jboot.json.timestampPattern = "yyyy-MM-dd HH:mm:ss"
```

## jboot v3.5.1 之前的配置

```
#是否启用大写转换，比如 user_age 自动转为为 UserAge
jboot.web.camelCaseJsonStyleEnable = true

#是否转换任何地方，比如 map.put("my_field") 默认输出为 myField
jboot.web.camelCaseToLowerCaseAnyway = false

#配置输出的时间格式
jboot.web.jsonTimestampPattern
```

## 通过 Java 代码配置

更多的配置可以通过 JFinalJsonKit 工具类来进行配置。

## 其他

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

