package io.jboot.test.seata.account;

public interface IAccountService {
	public boolean deposit(Integer accountId, Integer money);
}
