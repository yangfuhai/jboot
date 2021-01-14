package io.jboot.test.db.simple;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.app.JbootApplication;
import io.jboot.db.JbootDb;
import io.jboot.db.model.Columns;
import io.jboot.test.db.model.User;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/db")
public class DbController extends SuperDbController {


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

    }



    public void find1(User invocation){

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


    public void find4(){
        List<Record> users = JbootDb.find("user",Columns.create());
        renderJson(users);
    }


    public void find5(){
        List<Record> users = JbootDb.find("user",Columns.create("login_name","aaa"));
        renderJson(users);
    }


    public void find6(){
        List<Record> users = JbootDb.use().find("user",Columns.create("login_name","aaa"));
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
        List<Record> users = JbootDb.use().find("user",Columns.create("login_name",true));
        renderJson(users);
    }


    public void find9(){
        User dao = new User();
        Page<User> page = dao.paginateByColumns(getInt("page",1),10,Columns.create());
        renderJson(page);
    }



    public void del1(){
        User dao = new User();
        dao.batchDeleteByIds("1",2,3);
        renderJson(Ret.ok());
    }


    public void del2(){
        User dao = new User();
        dao.set("id",1);
        dao.delete();
        renderJson(Ret.ok());
    }

    public void save1(){
        User user = new User();
        user.set("login_name","michael");
        user.set("nickname","michael123");
        user.save();
        renderJson(Ret.ok());
    }


    public void safeMode(){
        User dao = new User();

        Columns columns = Columns.safeMode();
        columns.in("user.`id`",1,2,3,4);
        columns.likeAppendPercent("login_name",null);


        List<User> users = dao.leftJoin("article").as("a").on("user.id=a.user_id").findListByColumns(columns);
        renderJson(users);
    }


    public void use(){
        User dao = new User();

        Columns columns = Columns.create();
        columns.in("user.`id`",1,2,3,4);

        User newDao = (User) dao.use("aaa");
//        User newDao = (User) dao.useFirst("aaa");


//        List<User> users = newDao.findListByColumns(columns);
        List<User> users = newDao.leftJoin("article").as("a").on("user.id=a.user_id").findListByColumns(columns);
        renderJson(users);
    }
}
