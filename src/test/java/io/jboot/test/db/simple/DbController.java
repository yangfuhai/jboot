package io.jboot.test.db.simple;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.app.JbootApplication;
import io.jboot.db.model.Columns;
import io.jboot.test.db.model.User;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/db")
public class DbController extends JbootController {


    public static void main(String[] args) {

        //设置 数据源 的相关信息
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.db.model");
        JbootApplication.setBootArg("undertow.devMode", "false");

        //启动应用程序
        JbootApplication.run(args);

        Columns columns = Columns.create();
        columns.between("id",1,5);
        List<User> users = new User().findListByColumns(columns);
        System.out.println(Arrays.toString(users.toArray()));

    }


    public void index() {
        List<Record> records = Db.find("select * from `user`");
        renderJson(records);
    }

    public void find1(){

        User dao = new User();

        Columns columns = Columns.create();
        columns.between("id",1,5);

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }

    public void find2(){

        User dao = new User();

        Columns columns = Columns.create();
        columns.in("id",1,2,3,4);

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }

    public void find3(){

        User dao = new User();

        Columns columns = Columns.create();
        columns.in("id",1,2,3,4);
        columns.likeAppendPercent("login_name","c");

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }

}
