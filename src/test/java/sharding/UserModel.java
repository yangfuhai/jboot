package sharding;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */

@Table(tableName = "tb_user",
        primaryKey = "id",
        actualDataNodes = "db${0..1}.tb_user${0..2}",//db${0..1} 表示有数据库db0,db1，tb_user${0..2} 表示有三张表 tb_user0,tb_user1,tb_user2
        tableShardingStrategyConfig = UserTableShardingStrategyConfig.class,//分表策略
        databaseShardingStrategyConfig = UseDatabaseShardingStrategyConfig.class //分库策略
)
public class UserModel extends JbootModel<UserModel> {


    public String getId() {
        return get("id");
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }
}
