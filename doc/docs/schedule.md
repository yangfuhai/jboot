# 任务调度

## 目录
- 基本任务调度
- 分布式任务调度

## 基本任务调度

**方案1：**
通过 @Cron 注解，这个需要依赖 cron4j 框架：

```java
//1分钟执行一次
@Cron("*/1 * * * *")
public class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("task running...");
    }
}
```


Cron表达式详解：

Cron 表达式最多只允许五部分，每部分用空格分隔开来，这五部分从左到右依次表示分、时、天、月、周，其具体规则如下：
- 分 ：从 0 到 59
- 时 ：从 0 到 23
- 天 ：从 1 到 31，字母 L 可以表示月的最后一天
- 月 ：从 1 到 12，可以别名：jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov" and "dec"
- 周 ：从 0 到 6，0 表示周日，6 表示周六，可以使用别名： "sun", "mon", "tue", "wed", "thu", "fri" and "sat"

如上五部分的分、时、天、月、周又分别支持如下字符，其用法如下：
- 数字 n：表示一个具体的时间点，例如 5 * * * * 表示 5 分这个时间点时执行
- 逗号 , ：表示指定多个数值，例如 3,5 * * * * 表示 3 和 5 分这两个时间点执行
- 减号 -：表示范围，例如 1-3 * * * * 表示 1 分、2 分再到 3 分这三个时间点执行
- 星号 *：表示每一个时间点，例如 * * * * * 表示每分钟执行
- 除号 /：表示指定一个值的增加幅度。
  - 例如 */5表示每隔5分钟执行一次（序列：0:00, 0:05, 0:10, 0:15 等等）。
  - 再例如3-18/5 * * * * 是指在从3到18分钟值这个范围之中每隔5分钟执行一次（序列：0:03, 0:08, 0:13, 0:18, 1:03, 1:08 等等）。

**方案2：**
Jboot 通过 `ScheduledThreadPoolExecutor` 封装一个轻量的任务调度框架，使用方法如下：

```java
@FixedDelay(period = 5)
public class MyTask implements Runnable {

    @Override
    public void run() {
       System.out.println("task running...");
    }
}
```

**方案3：**
使用 JFinal 自带的任务调度方案，参考文档：https://www.jfinal.com/doc/9-1


## 分布式任务调度
分布式任务在以上的 **基本任务调度** 的基础上，只需要添加 `@EnableDistributedRunnable` 注解即可，例如：

示例1：

```java
@Cron("*/1 * * * *")
@EnableDistributedRunnable
public class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("task running...");
    }
}
```

示例2：

```java
@FixedDelay(period = 5)
@EnableDistributedRunnable
public class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("task running...");
    }
}
```

注意：分布式任务调度需要依赖 redis，因此，在使用分布式任务之前需要做好如下配置：

```
jboot.redis.host = 127.0.0.1
jboot.redis.password = 
```