在使用 fescar 进行本测试之前，请先做好如下环境配置：
1、创建数据库 fescar ，并执行 commons 下的 sql 创建对于的表。
2、下载 fescar server 到本地，并启动之
    1）server 下载地址：https://github.com/alibaba/fescar/releases
    2）启动 fescar 的命令：sh fescar-server.sh 8091 ./data



其他注意事项：
1）jboot.fescar.txServiceGroup 配置的值要注意和 file.conf 里的 vgroup_mapping.xxx 保持一致
3）jboot.rpc.filter=fescar ##Fescar在Dubbo中的事务传播过滤器
