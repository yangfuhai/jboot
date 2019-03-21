package io.jboot.test.fescar.business;


import io.jboot.aop.annotation.Bean;
import io.jboot.components.fescar.annotation.JbootGlobalTransactional;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.fescar.account.IAccountService;
import io.jboot.test.fescar.commons.Account;
import io.jboot.test.fescar.stock.IStockService;

@Bean // 设置为自动发现
public class BusinessServiceProvider extends JbootServiceBase<Account> {
	@RPCInject
	private IAccountService accountService;
	@RPCInject
	private IStockService stockService;

	@JbootGlobalTransactional(timeoutMills = 300000, name = "Dubbo_Fescar_Account_Service")
	public boolean deposit(Integer accountId) {
		accountService.deposit(accountId, 1000);
		stockService.deposit(accountId, 2000);
		return true;
	}

}
