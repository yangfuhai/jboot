package io.jboot.test.aop.cache;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/aopcache")
public class AopCacheController extends JbootController {

    @Inject
    private CommentService commentService;

    private String id = "myId";

    public void index() {
        renderText("text from : " + commentService.getCommentById(id));
    }


    public void cache() {
        renderText("text from : " + commentService.getCommentByIdWithCache(id));
    }

    public void cacheTime() {
        renderText("text from : " + commentService.getCommentByIdWithCache(id));
    }


    public void updateCache() {
        renderText("text from : " + commentService.updateCache(id));
    }


    public void delCache() {
        commentService.delCache(id);
        renderText("text from : " + commentService.getCommentByIdWithCache(id));
    }

}
