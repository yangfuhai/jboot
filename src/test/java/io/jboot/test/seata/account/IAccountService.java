package io.jboot.test.seata.account;

public interface IAccountService {

	public boolean deposit(Integer accountId, Integer money);

	public boolean updateStore(String  account, Integer money);

	public boolean updateRollbackStore(String  account, Integer money);

	public boolean update(String  accountId, Integer money);
	
}
