在开始之前，先启动 nacos，启动建议使用 docker 的方式，更加方便。

Clone 项目

```shell
git clone https://github.com/nacos-group/nacos-docker.git
cd nacos-docker
```

单机模式 Derby

```shell
docker-compose -f example/standalone-derby.yaml up
```


访问Nacos 控制台 ：[http://127.0.0.1:8848/nacos/
](http://127.0.0.1:8848/nacos/)


更多参考 https://nacos.io/zh-cn/docs/quick-start-docker.html