在使用 seata 进行本测试之前，请先做好如下环境配置：
1、创建数据库 seata ，并执行 commons 下的 sql 创建对于的表。
2、下载 seata server 到本地，并启动之
    1）server 下载地址：https://github.com/seata/seata/releases
    2）启动 seata 的命令：sh seata-server.sh 8091 ./data



其他注意事项：
1）jboot.seata.txServiceGroup 配置的值要注意和 file.conf 里的 vgroup_mapping.xxx 保持一致
3）jboot.rpc.filter=seata ##seata在Dubbo中的事务传播过滤器
