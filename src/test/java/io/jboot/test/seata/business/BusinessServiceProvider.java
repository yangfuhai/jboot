package io.jboot.test.seata.business;


import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.service.JbootServiceBase;
import io.jboot.support.seata.annotation.SeataGlobalTransactional;
import io.jboot.test.seata.account.IAccountService;
import io.jboot.test.seata.commons.Account;
import io.jboot.test.seata.stock.IStockService;

public class BusinessServiceProvider extends JbootServiceBase<Account> {

	@RPCInject
	private IAccountService accountService;
	@RPCInject
	private IStockService stockService;

	@SeataGlobalTransactional(timeoutMills = 300000, name = "Dubbo_Seata_Account_Service")
	public boolean deposit(Integer accountId) {
		accountService.deposit(accountId, 1000);
		stockService.deposit(accountId, 2000);
		return true;
	}

}
