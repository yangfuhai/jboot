package io.jboot.test.join.service;

import com.jfinal.aop.Inject;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.join.model.Author;

import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class AuthorService extends JbootServiceBase<Author> {


    @Inject
    private ArticleService articleService;


    public List<Author> findListWithArticle(){
        List<Author> authors = DAO.findAll();
        articleService.joinMany(authors,"author_id");
        return authors;
    }
}
