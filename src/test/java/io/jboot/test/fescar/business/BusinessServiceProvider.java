package io.jboot.test.fescar.business;


import com.alibaba.fescar.core.context.RootContext;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.service.JbootServiceBase;
import io.jboot.support.fescar.annotation.FescarGlobalTransactional;
import io.jboot.test.fescar.account.IAccountService;
import io.jboot.test.fescar.commons.Account;
import io.jboot.test.fescar.stock.IStockService;
@Bean
public class BusinessServiceProvider extends JbootServiceBase<Account> {

	@RPCInject(version="1.0")
	private IAccountService accountService;
	@RPCInject(version="1.0")
	private IStockService stockService;

	@FescarGlobalTransactional(timeoutMills = 300000, name = "Dubbo_Fescar_Business_Service")
	public boolean deposit(Integer accountId) {
		   System.out.println("开始全局事务，XID = " + RootContext.getXID());
		accountService.deposit(accountId, 1000);
		stockService.deposit(accountId, 2000);
		try {
		int  x  = 1/1;
		}catch (Exception e) {
			throw new RuntimeException();
		}
		return true;
	}

}
