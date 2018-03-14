package sharding;

import io.jboot.Jboot;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package sharding
 */
public class Stater {


    /**
     * 执行main方法之前，请先在mysql数据库上创建两个库：jbootsharding0 和 jbootsharding1
     * 然后用 jbootsharding.sql 创建表
     * 然后再执行main方法
     *
     * @param args
     */
    public static void main(String[] args) {


        /**
         * 数据源1
         */
        Jboot.setBootArg("jboot.datasource.db0.type", "mysql");
        Jboot.setBootArg("jboot.datasource.db0.url", "jdbc:mysql://127.0.0.1:3306/jbootsharding0");
        Jboot.setBootArg("jboot.datasource.db0.user", "root");

        /**
         * 数据源2
         */
        Jboot.setBootArg("jboot.datasource.db1.type", "mysql");
        Jboot.setBootArg("jboot.datasource.db1.url", "jdbc:mysql://127.0.0.1:3306/jbootsharding1");
        Jboot.setBootArg("jboot.datasource.db1.user", "root");


        /**
         * 主数据源：包含了 两个子数据源 db0和db1
         */
        Jboot.setBootArg("jboot.datasource.shardingEnable", "true");
        Jboot.setBootArg("jboot.datasource.shardingDatabase", "db0,db1");

        //hystrix配置
        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.html");//配置 Hystrix Dashboard 的监控路径,方便查看hystrix对sql的监控


        Jboot.run(args);
    }
}
