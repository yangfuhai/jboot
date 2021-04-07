package io.jboot.test.db.tx;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.app.JbootApplication;
import io.jboot.db.model.Columns;
import io.jboot.test.db.model.User;
import io.jboot.test.db.simple.SuperDbController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/tx")
public class TxTestController extends SuperDbController {


    public static void main(String[] args) {

        //设置 数据源 的相关信息
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.db.model");
//        JbootApplication.setBootArg("undertow.devMode", "false");

        //启动应用程序
        JbootApplication.run(args);

        Columns columns = Columns.create();
        columns.between("id",1,5);
        List<User> users = new User().findListByColumns(columns);


        System.out.println(Arrays.toString(users.toArray()));
        System.out.println(Db.find("select * from user"));

    }


    @Inject
    private TestService testService;

    public void index(){
        testService.test1();

        System.out.println("--2-->>>" + testService.test2());
        System.out.println("--3-->>>" + testService.test3());

        renderText("index...");
    }
}
