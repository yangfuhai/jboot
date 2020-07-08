package io.jboot.test.join.model;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "category",primaryKey = "id")
public class Category extends JbootModel<Category> {


    /**
     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
     `title` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
     `description` text COLLATE utf8mb4_unicode_ci,
     */



    public Long getId(){
        return getLong("id");
    }

    public void setId(Long id){
        set("id",id);
    }


    public String getTitle(){
        return getStr("title");
    }

    public void setTitle(String title){
        set("title",title);
    }

    public String getDescription(){
        return getStr("description");
    }

    public void setDescription(String description){
        set("description",description);
    }

}
