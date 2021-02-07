package io.jboot.test.db.clickhouse;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.app.JbootApplication;
import io.jboot.db.JbootDb;
import io.jboot.db.model.Columns;
import io.jboot.test.db.model.User;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/clickhouse")
public class ClickHouseController extends JbootController {


    public static void main(String[] args) {

        //设置 数据源 的相关信息
        JbootApplication.setBootArg("jboot.datasource.factory", "druid");
        JbootApplication.setBootArg("jboot.datasource.type", "clickhouse");
//        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:clickhouse://localhost:9000/tutorial");

        JbootApplication.setBootArg("jboot.datasource.driverClassName", "io.jboot.db.driver.OfficialClickHouseDriver");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:clickhouse://localhost:8123/tutorial");

//        JbootApplication.setBootArg("jboot.datasource.user", "root");
//        JbootApplication.setBootArg("jboot.datasource.password", "123456");
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.db.clickhouse");
//        JbootApplication.setBootArg("undertow.devMode", "false");

        //启动应用程序
        JbootApplication.run(args);

        Columns columns = Columns.create();
        columns.between("id",1,5);
        List<UserInfo> users = new UserInfo().findListByColumns(columns);


        System.out.println(Arrays.toString(users.toArray()));
        System.out.println(Db.find("select * from user_info"));

    }

    public void index() {
        List<Record> records = Db.find("select * from user_info");
        renderJson(records);
    }




    public void find1(){

        UserInfo dao = new UserInfo();

        Columns columns = Columns.create();
        columns.between("id",1,5);

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }

    public void find2(){

        UserInfo dao = new UserInfo();

        Columns columns = Columns.create();
        columns.in("id",1,2,3,4);

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }

    public void find3(){

        UserInfo dao = new UserInfo();

        Columns columns = Columns.create();
        columns.in("id",1,2,3,4);

        List<User> users = dao.findListByColumns(columns);
        renderJson(users);
    }


    public void find4(){
        List<Record> users = JbootDb.find("user_info",Columns.create());
        renderJson(users);
    }


    public void find5(){
        List<Record> users = JbootDb.find("user_info",Columns.create("login_name","aaa"));
        renderJson(users);
    }


    public void find6(){
        List<Record> users = JbootDb.use().find("user_info",Columns.create("login_name","aaa"));
        renderJson(users);
    }

    public void find7(){
        User dao = new User();

        Columns columns = Columns.create();
        columns.in("u.id",1,2,3,4);
//        columns.likeAppendPercent("login_name","c");

//        List<User> users = dao.leftJoin("article","a","user.id=a.user_id").findListByColumns(columns);
        List<User> users = dao.loadColumns("u.id,a.id").alias("u").leftJoin("article").as("a").on("u.id=a.user_id").findListByColumns(columns);

        dao.findAll();

        renderJson(users);
    }




    public void find8(){
        List<Record> users = JbootDb.use().find("user_info",Columns.create("login_name",true));
        renderJson(users);
    }


    public void find9(){
        UserInfo dao = new UserInfo();
        Page<User> page = dao.paginateByColumns(getInt("page",1),2,Columns.create());
        renderJson(page);
    }



    public void del1(){
        UserInfo dao = new UserInfo();
        dao.batchDeleteByIds("1",2);
        renderJson(Ret.ok());
    }


    public void del2(){
        UserInfo dao = new UserInfo();
        dao.set("id",100);
        dao.delete();
        renderJson(Ret.ok());
    }

    public void save1(){
        UserInfo user = new UserInfo();
        user.set("id",101);
        user.set("age",20);
        user.set("name","张三");
        user.save();
        renderJson(Ret.ok());
    }

    public void update1(){
        UserInfo user = new UserInfo();
        user.set("id",100);
        user.set("name","李四");
        user.update();
        renderJson(Ret.ok());
    }


    public void safeMode(){
        UserInfo dao = new UserInfo();

        Columns columns = Columns.safeMode();
        columns.in("user.`id`",1,2,3,4);
        columns.likeAppendPercent("login_name",null);


        List<User> users = dao.leftJoin("article").as("a").on("user.id=a.user_id").findListByColumns(columns);
        renderJson(users);
    }


    public void use(){
        UserInfo dao = new UserInfo();

        Columns columns = Columns.create();
        columns.in("user.`id`",1,2,3,4);

        User newDao = (User) dao.use("aaa");
//        User newDao = (User) dao.useFirst("aaa");


//        List<User> users = newDao.findListByColumns(columns);
        List<User> users = newDao.leftJoin("article").as("a").on("user.id=a.user_id").findListByColumns(columns);
        renderJson(users);
    }
}
