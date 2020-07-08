package io.jboot.test.join.service;

import com.jfinal.aop.Inject;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.join.model.Article;

import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class ArticleService extends JbootServiceBase<Article> {

    @Inject
    private AuthorService authorService;

    @Inject
    private CategoryService categoryService;


    public List<Article> findListWithAuthorAndCategorys(){
        List<Article> articles = DAO.findAll();
        authorService.join(articles,"author_id");
        categoryService.joinManyByTable(articles, "article_category","category_id","article_id");
        return articles;
    }
}
