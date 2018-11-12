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


        Jboot.setBootArg("jboot.model.idCacheEnable", true);


        Jboot.setBootArg("jboot.datasource.sharding.shardingConfigYaml", "sharding.yaml");
        Jboot.setBootArg("jboot.datasource.sharding.table", "tb_user");
//        Jboot.setBootArg("jboot.model.scan", "sharding");


        //hystrix配置
        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.html");//配置 Hystrix Dashboard 的监控路径,方便查看hystrix对sql的监控


        Jboot.run(args);
    }
}
