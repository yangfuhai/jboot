package io.jboot.test.seata.business;


import com.jfinal.aop.Inject;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.service.JbootServiceBase;
import io.jboot.support.seata.annotation.SeataGlobalTransactional;
import io.jboot.test.seata.account.IAccountService;
import io.jboot.test.seata.commons.Account;
import io.jboot.test.seata.stock.IStockService;

public class BusinessServiceProvider extends JbootServiceBase<Account> {

	@Inject
	private IAccountService accountService;
	@Inject
	private IStockService stockService;

	public boolean deposit(Integer accountId) {
		accountService.deposit(accountId, 100);
		stockService.deposit(accountId, 200);
		return true;
	}

}
