# 项目构建

## 目录

- 单模块 maven 项目构建
- 多模块 maven 项目构建

## 单模块 maven 项目构建

在单一模块的maven项目开发中，我们通常在 `src/main/resources` 编写我们的配置文件，因此，在 maven 构建的时候，我们需要添加如下配置：

```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*.*</include>
        </includes>
        <filtering>false</filtering>
    </resource>
</resources>
```
把 `src/main/resources` 目录下的文件拷贝到 classpath 目录去。

同时，在 jboot 开发的应用中，建议把 html、css、js等资源文件存放在 `src/main/webapp` 目录下。


以上配置完毕之后，我们需要再配置 `maven-assembly-plugin` 插件，配置内容如下。

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
            <configuration>
                <recompressZippedFiles>false</recompressZippedFiles>
                <appendAssemblyId>false</appendAssemblyId>
                <descriptors>
                    <descriptor>package.xml</descriptor>
                </descriptors>
                <outputDirectory>${project.build.directory}/</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

package.xml

```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.0.0 http://maven.apache.org/xsd/assembly-2.0.0.xsd">

    <id>release</id>

    <formats>
        <format>dir</format>
        <format>zip</format>
        <!-- <format>tar.gz</format> -->
    </formats>

    <!-- 打 zip 设置为 true 时，会在 zip 包中生成一个根目录，打 dir 时设置为 false 少层目录 -->
    <includeBaseDirectory>false</includeBaseDirectory>

    <fileSets>

        <fileSet>
            <directory>${basedir}/src/main/resources</directory>
            <outputDirectory>config</outputDirectory>
        </fileSet>

        <fileSet>
            <directory>${basedir}/target/classes/webapp</directory>
            <outputDirectory>webapp</outputDirectory>
        </fileSet>

        <!-- 项目根下面的脚本文件 copy 到根目录下 -->
        <fileSet>
            <directory>${basedir}</directory>
            <outputDirectory></outputDirectory>
            <!-- 脚本文件在 linux 下的权限设为 755，无需 chmod 可直接运行 -->
            <fileMode>755</fileMode>
            <includes>
                <include>*.sh</include>
                <include>*.bat</include>
            </includes>
        </fileSet>
    </fileSets>

    <!-- 依赖的 jar 包 copy 到 lib 目录下 -->
    <dependencySets>
        <dependencySet>
            <outputDirectory>lib</outputDirectory>
        </dependencySet>
    </dependencySets>

</assembly>
```

在 项目的根目录下，创建 `jboot.sh` 文件，内容如下：

```
#!/bin/bash
# ----------------------------------------------------------------------
# name:         jboot.sh
# version:      1.0
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : ./jboot.sh {start, stop, restart}
# ----------------------------------------------------------------------

# 启动入口类，该脚本文件用于别的项目时要改这里
MAIN_CLASS=io.jboot.app.JbootApplication
COMMAND="$1"

if [[ "$COMMAND" != "start" ]] && [[ "$COMMAND" != "stop" ]] && [[ "$COMMAND" != "restart" ]]; then
	echo "./jboot.sh {start, stop, restart}"
	exit 0
fi



# Java 命令行参数，根据需要开启下面的配置，改成自己需要的，注意等号前后不能有空格
# JAVA_OPTS="-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
# JAVA_OPTS="-Dundertow.port=80 -Dundertow.host=0.0.0.0"

# 生成 class path 值
APP_BASE_PATH=$(cd `dirname $0`; pwd)
CP=${APP_BASE_PATH}/config:${APP_BASE_PATH}/lib/*

function start()
{
    # 运行为后台进程，并在控制台输出信息
    java -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} &

    # 运行为后台进程，并且不在控制台输出信息
    # nohup java -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} >/dev/null 2>&1 &

    # 运行为后台进程，并且将信息输出到 output.log 文件
    # nohup java -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} > output.log &

    # 运行为非后台进程，多用于开发阶段，快捷键 ctrl + c 可停止服务
    # java -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS}
}

function stop()
{
    kill `pgrep -f ${MAIN_CLASS}` 2>/dev/null

    # 以下代码与上述代码等价
    # kill $(pgrep -f ${MAIN_CLASS}) 2>/dev/null

}

if [[ "$COMMAND" == "start" ]]; then
	start
elif [[ "$COMMAND" == "stop" ]]; then
    stop
else
    stop
    start
fi
```

以上内容配置完毕之后，支持 maven 命令 `mvn clean install` ，待命令执行完毕之后，会在 `target` 目录下生成一个文件名为： `项目-版本` 的文件夹。

复制该文件夹到服务器，然后执行里面的 `jboot.sh start` 命令即可上线。


## 多模块 maven 项目构建

多模块项目在以上配置的基础上，添加 `maven-resources-plugin` maven 插件，用于拷贝其他maven模块的资源文件和html等内容到此运行模块。

maven 配置如下：

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-resources</id>
            <phase>validate</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${basedir}/target/classes/webapp</outputDirectory>
                <resources>
                    <resource>
                        <directory>${basedir}/../otherModule1/src/main/webapp</directory>
                    </resource>
                    <resource>
                        <directory>${basedir}/../otherModule2/src/main/webapp</directory>
                    </resource>
                    <resource>
                        <directory>${basedir}/../otherModule3/src/main/webapp</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

这部分可以参考 jpress 项目，网址：https://gitee.com/fuhai/jpress/blob/v2.0/starter/pom.xml