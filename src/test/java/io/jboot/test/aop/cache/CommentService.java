package io.jboot.test.aop.cache;


import io.jboot.test.db.model.User;

import java.util.List;

public interface CommentService {

    public String getCommentById(String id);

    public String getCommentByIdWithCache(String id);

    public String getCommentByIdWithCacheTime(String id);

    public String updateCache(String id);

    public void delCache(String id);

    public List<User> findList();

    public User[] findArray();

}
