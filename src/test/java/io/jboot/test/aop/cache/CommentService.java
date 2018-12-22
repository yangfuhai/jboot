package io.jboot.test.aop.cache;


public interface CommentService {

    public String getCommentById(String id);

    public String getCommentByIdWithCache(String id);

    public String getCommentByIdWithCacheTime(String id);

    public String updateCache(String id);

    public void delCache(String id);

}
