package io.jboot.test.seata.stock;


import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.seata.commons.Stock;

@RPCBean
@Bean
public class StockServiceProvider extends JbootServiceBase<Stock> implements IStockService {

    private static Stock dao = new Stock().dao();

    public boolean deposit(Integer accountId, Integer stock) {

        Stock account = dao.findById(accountId);
        account.set("Stock", account.getInt("Stock") + stock);

        if (stock > 1000) {
            throw new RuntimeException(StockServiceProvider.class.getSimpleName()+"Dubbo Seata Exception By Hobbit");
        }

        return account.update();
    }

}
