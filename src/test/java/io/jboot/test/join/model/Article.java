package io.jboot.test.join.model;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "article",primaryKey = "id")
public class Article extends JbootModel<Article> {

    /**
     *   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
     *   `author_id` int(11) unsigned DEFAULT NULL,
     *   `title` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
     *   `content` text COLLATE utf8mb4_unicode_ci,
     */

    public Long getId(){
        return getLong("id");
    }

    public void setId(Long id){
        set("id",id);
    }

    public Long getAuthorId(){
        return getLong("author_id");
    }

    public void setAuthorId(Long id){
        set("author_id",id);
    }


    public String getTitle(){
        return getStr("title");
    }

    public void setTitle(String title){
        set("title",title);
    }

    public String getContent(){
        return getStr("content");
    }

    public void setContent(String content){
        set("content",content);
    }


}
