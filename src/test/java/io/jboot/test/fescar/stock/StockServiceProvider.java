package io.jboot.test.fescar.stock;


import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.fescar.account.IAccountService;
import io.jboot.test.fescar.commons.Stock;
@RPCBean
public class StockServiceProvider extends JbootServiceBase<Stock> implements  IAccountService{
private static Stock dao = new  Stock().dao(); 
	public boolean deposit(Integer accountId, Integer stock){
		
		Stock  account =  dao.findById(accountId);
		account.set("Stock", account.getInt("Stock")+stock);
		
		if(stock>1000){
			throw new RuntimeException("Dubbo Fescar Exception By Hobbit");
		}
		
		return account.update();
	}

}
