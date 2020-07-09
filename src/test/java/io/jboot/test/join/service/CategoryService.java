package io.jboot.test.join.service;

import com.jfinal.aop.Inject;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.join.model.Category;

import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class CategoryService extends JbootServiceBase<Category> {

    @Inject
    private ArticleService articleService;

    public List<Category> findListWithArticles(){
        List<Category> categories = DAO.findAll();
        articleService.joinManyByTable(categories,"article_category","category_id","article_id");
        return categories;
    }
}
