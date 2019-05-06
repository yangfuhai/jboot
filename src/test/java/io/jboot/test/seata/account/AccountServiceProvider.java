package io.jboot.test.seata.account;


import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.seata.commons.Account;

@RPCBean
public class AccountServiceProvider extends JbootServiceBase<Account> implements IAccountService {
    private static Account dao = new Account().dao();

    public boolean deposit(Integer accountId, Integer money) {

        Account account = dao.findById(accountId);
        account.set("Money", account.getInt("Money") + money);

        if (money > 1000) {
            throw new RuntimeException(AccountServiceProvider.class.getSimpleName()+"Dubbo Seata Exception By Hobbit");
        }

        return account.update();
    }

}
