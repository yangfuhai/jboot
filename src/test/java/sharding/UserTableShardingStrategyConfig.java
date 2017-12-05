package sharding;

import com.google.common.collect.Sets;
import io.shardingjdbc.core.api.algorithm.sharding.ListShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.ShardingValue;
import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingjdbc.core.routing.strategy.ShardingStrategy;

import java.util.Collection;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */
public class UserTableShardingStrategyConfig implements ShardingStrategyConfiguration {

    @Override
    public ShardingStrategy build() {
        return shardingStrategy;
    }


    private ShardingStrategy shardingStrategy = new ShardingStrategy() {

        @Override
        public Collection<String> getShardingColumns() {
            //根据id进行分表
            return Sets.newHashSet("id");
        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
            ListShardingValue shardingValue = (ListShardingValue) shardingValues.stream().findFirst().get();

            String tableName = "tb_user" + Math.abs(shardingValue.getValues().iterator().next().toString().hashCode()) % 3;

            System.out.println("插入数据到表：" + tableName);

            //返回通过计算得到的表
            return Sets.newHashSet(tableName);

        }
    };


}
