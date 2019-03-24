package io.jboot.test.fescar.stock;


import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.fescar.commons.Stock;

@RPCBean
public class StockServiceProvider extends JbootServiceBase<Stock> implements IStockService {

    private static Stock dao = new Stock().dao();

    public boolean deposit(Integer accountId, Integer stock) {

        Stock stockHobbit = dao.findById(accountId);
        stockHobbit.set("Stock", stockHobbit.getInt("Stock") + stock);

        if (stock > 1000) {
            throw new RuntimeException("Dubbo Fescar Exception By Hobbit");
        }

        return stockHobbit.update();
    }

}
