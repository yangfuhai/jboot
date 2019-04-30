package io.jboot.test.seata.stock;

public interface IStockService {
	public boolean deposit(Integer accountId, Integer stock);

}
