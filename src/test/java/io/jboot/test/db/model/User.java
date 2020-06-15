package io.jboot.test.db.model;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "user",primaryKey = "id")
public class User extends JbootModel {

    public String getSexString(){
         return "男";
    }

    public String getId(){
        return "111";
    }
}
