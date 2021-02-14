---
sidebar: auto
---
# Jboot 性能测试

## 测试方式

通过 apache benchmark 工具进行压力测试

## 测试环境

* JDK信息：
	* java version "1.8.0_25"
	* Java(TM) SE Runtime Environment (build 1.8.0_25-b17)
	* Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02, mixed mode)

* 硬件信息
	* 处理器：2.3 GHz Intel Core i7
	* 内存：16 GB 1600 MHz DDR3
	* 系统：macOS 10.13.4 (17E202)
	* 硬件：MacBook Pro (Retina, 15-inch, Late 2013

## 测试代码

测试代码：

```java
@RequestMapping("/")
public class HelloDemo extends JbootController {

    public static void main(String[] args) {
        Jboot.setBootArg("jboot.mode","product");
        Jboot.run(args);
    }

    public void index() {
       renderText("hello jboot ...");
    }
}
```

代码含义：

* 默认使用 undertow 服务器
* 把配置设置为生产模式，生产模式不会打印调试日志


## 测试结果

模拟10个并发，10000次访问：

`ab -c10 -n10000 http://localhost:8080/`


```
This is ApacheBench, Version 2.3 <$Revision: 1807734 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8080

Document Path:          /
Document Length:        15 bytes

Concurrency Level:      10
Time taken for tests:   0.716 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      2330000 bytes
HTML transferred:       150000 bytes
Requests per second:    13970.07 [#/sec] (mean)
Time per request:       0.716 [ms] (mean)
Time per request:       0.072 [ms] (mean, across all concurrent requests)
Transfer rate:          3178.74 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.1      0       1
Processing:     0    0   0.1      0       3
Waiting:        0    0   0.1      0       3
Total:          0    1   0.1      1       4

Percentage of the requests served within a certain time (ms)
  50%      1
  66%      1
  75%      1
  80%      1
  90%      1
  95%      1
  98%      1
  99%      1
 100%      4 (longest request)
 ```