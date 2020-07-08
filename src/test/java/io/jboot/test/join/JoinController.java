package io.jboot.test.join;

import com.jfinal.aop.Aop;
import io.jboot.app.JbootApplication;
import io.jboot.test.join.model.Article;
import io.jboot.test.join.model.Author;
import io.jboot.test.join.service.ArticleService;
import io.jboot.test.join.service.AuthorService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/join")
public class JoinController extends JbootController {


    public static void main(String[] args) {

        //设置 数据源 的相关信息
        JbootApplication.setBootArg("jboot.datasource.type", "mysql");
        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jboot_join_demo");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");
        JbootApplication.setBootArg("jboot.model.unscanPackage", "*");
        JbootApplication.setBootArg("jboot.model.scanPackage", "io.jboot.test.join.model");
        JbootApplication.setBootArg("undertow.devMode", "false");

        //启动应用程序
        JbootApplication.run(args);

    }


    public void articles() {
        List<Article> articles = Aop.get(ArticleService.class).findListWithAuthorAndCategorys();
        renderJson(articles);
    }


    public void authors() {
        List<Author> authors = Aop.get(AuthorService.class).findListWithArticle();
        renderJson(authors);
    }

}
