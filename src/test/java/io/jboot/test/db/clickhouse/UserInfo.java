package io.jboot.test.db.clickhouse;

import com.alibaba.fastjson.annotation.JSONField;
import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "user_info",primaryKey = "id")
public class UserInfo extends JbootModel {

    @JSONField(name = "sex")
    public String getSexString(){
         return "男";
    }

}
