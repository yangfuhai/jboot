package sharding;

import com.google.common.collect.Sets;
import io.shardingsphere.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingsphere.core.routing.strategy.ShardingStrategy;

import java.util.Collection;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */
public class UserTableShardingStrategyConfig implements ShardingStrategyConfiguration {
//
//    @Override
//    public ShardingStrategy build() {
//        return shardingStrategy;
//    }


    private ShardingStrategy shardingStrategy = new ShardingStrategy() {

        @Override
        public Collection<String> getShardingColumns() {
            //根据id进行分表,可以根据多个字段
            return Sets.newHashSet("id");
        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {

            ListShardingValue shardingValue = (ListShardingValue) ((List) shardingValues).get(0);
            String value = (String) ((List) shardingValue.getValues()).get(0);

            //得到要插入 或者 查询的表
            String tableName = "tb_user" + Math.abs(value.hashCode()) % 3;

            System.out.println("命中数据到表：" + tableName);

            //返回通过计算得到的表
            return Sets.newHashSet(tableName);

        }
    };

}
