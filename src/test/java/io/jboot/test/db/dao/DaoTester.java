package io.jboot.test.db.dao;

import com.alibaba.fastjson.JSON;
import io.jboot.app.JbootApplication;
import io.jboot.db.model.Columns;
import io.jboot.test.db.model.User;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/dao")
public class DaoTester extends JbootController {


    public static void main(String[] args) {

        //设置 datasource 1 的相关信息
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.db.model");



        //启动应用程序
        JbootApplication.run(args);

    }


    public void index() {
        User dao = new User().dao();
        renderJson(JSON.toJSON(dao.findCountByColumns(Columns.EMPTY)));
    }


    public void error() {
        User dao = new User().dao();
        dao.save();
    }

}
