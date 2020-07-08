package io.jboot.test.join.model;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "author",primaryKey = "id")
public class Author extends JbootModel<Author> {


    /**
     *  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
     *   `nickname` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
     *   `email` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
     */



    public Long getId(){
        return getLong("id");
    }

    public void setId(Long id){
        set("id",id);
    }


    public String getNickname(){
        return getStr("nickname");
    }

    public void setNickname(String nickname){
        set("nickname",nickname);
    }

    public String getEmail(){
        return getStr("email");
    }

    public void setEmail(String email){
        set("email",email);
    }
   
}
