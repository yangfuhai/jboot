package sharding;

import com.google.common.collect.Sets;
import io.shardingjdbc.core.api.algorithm.sharding.ListShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.ShardingValue;
import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingjdbc.core.routing.strategy.ShardingStrategy;

import java.util.Collection;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package sharding
 */
public class UseDatabaseShardingStrategyConfig implements ShardingStrategyConfiguration {

    @Override
    public ShardingStrategy build() {
        return shardingStrategy;
    }


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

            String dbName = "db" + Math.abs(value.hashCode()) % 2;
            System.out.println("命中数据到的数据库：" + dbName);

            return Sets.newHashSet(dbName);

        }
    };

}
