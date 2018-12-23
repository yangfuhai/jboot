package io.jboot.test.db.mutildatasource;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/mutilds")
public class MutilDatasourceController extends JbootController {


    public static void main(String[] args) {

        //设置 datasource 1 的相关信息
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");


        //设置 datasource 2 的相关信息
        JbootApplication.setBootArg("jboot.datasource.ds2.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.ds2.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.ds2.user", "root");


        //启动应用程序
        JbootApplication.run(args);

    }


    public void index() {
        List<Record> records = Db.find("select * from `user`");
        renderJson(JSON.toJSON(records));
    }


    public void ds2() {
        List<Record> records = Db.use("ds2").find("select * from `user`");
        renderJson(JSON.toJSON(records));
    }

}
