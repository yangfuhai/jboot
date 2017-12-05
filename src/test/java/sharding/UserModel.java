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
        actualDataNodes = "main.tb_user${0..2}", // 具体的表,main 是默认数据源的名称
        tableShardingStrategyConfig = UserTableShardingStrategyConfig.class //分表策略
)
public class UserModel extends JbootModel<UserModel> {


    public UserModel(String id, String name) {
        setId(id);
        setName(name);
    }

    public UserModel() {
    }


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
