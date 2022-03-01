package io.jboot.test.db.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;
import io.jboot.web.json.JsonIgnore;

@Table(tableName = "user",primaryKey = "id")
public class User extends JbootModel {

    @JSONField(name = "sex")
    public String getSexString(){
         return "男";
    }

    @JsonIgnore
//@JSONField(name = "myId")
    public String getId(){
        return "111";
    }


    public Integer getUserId(){
        return (Integer) get("user_id");
    }
}
