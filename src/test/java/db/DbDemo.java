/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package db;

import db.model.Article;
import io.jboot.Jboot;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;


@RequestMapping("/db")
public class DbDemo extends JbootController {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");
        Jboot.setBootArg("jboot.model.scan", "db.model");


        Jboot.run(args);
    }


    public void index() {

        renderText("hello jboot ...");
    }

    public void isNull() {

        Article dao = new Article();
        Columns columns = Columns.create().is_null("thumbnail");
        columns.gt("id", 2);
        List<Article> list = dao.findListByColumns(columns);
        renderJson(list);

    }


    public void isNull1() {

        Article dao = new Article();

        List<Article> list = dao.findListByColumns(Columns.create().is_null("thumbnail"));
        renderJson(list);

    }

    public void isNotNull() {
        Article dao = new Article();

        Columns columns = Columns.create().is_not_null("thumbnail");
        columns.gt("id", 2);
        List<Article> list = dao.findListByColumns(columns);
        renderJson(list);
    }


    public void isNotNull1() {
        Article dao = new Article();
        List<Article> list = dao.findListByColumns(Columns.create().is_not_null("thumbnail"));
        renderJson(list);
    }

}



