# Json 配置

## jboot v3.5.1（包括 3.5.1） 之后的配置

```
#是否启用大写转换，比如 user_age 自动转为为 UserAge
jboot.json.camelCaseJsonStyleEnable = true

#是否转换任何地方，比如 map.put("my_field") 默认输出为 myField
jboot.json.camelCaseToLowerCaseAnyway = false

#是否跳过 null 值输出，map.put("key",null)，则 key 值不输
jboot.json.skipNullValueField = true

#配置输出的时间格式
jboot.json.jsonTimestampPattern
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

