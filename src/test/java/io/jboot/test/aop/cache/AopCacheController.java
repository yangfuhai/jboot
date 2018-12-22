package io.jboot.test.aop.cache;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/aopcache")
public class AopCacheController extends JbootController {

    @Inject
    private CommentService commentService;


    public void index() {
        renderText("text from : " + commentService.getCommentById("index"));
    }


    public void cache() {
        renderText("text from : " + commentService.getCommentByIdWithCache("cache"));
    }

    public void cachetime() {
        renderText("text from : " + commentService.getCommentByIdWithCacheTime("cachetime"));
    }


    public void updateCache() {
        renderText("text from : " + commentService.updateCache("cache"));
    }


    public void delCache() {
        commentService.delCache("cache");
        renderText("del ok");
    }

}
