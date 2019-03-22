package io.jboot.test.fescar.account;


import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.fescar.commons.Account;

@RPCBean
public class AccountServiceProvider extends JbootServiceBase<Account> implements IAccountService {
    private static Account dao = new Account().dao();

    public boolean deposit(Integer accountId, Integer money) {

        Account account = dao.findById(accountId);
        account.set("Money", account.getInt("Money") + money);

        if (money > 1000) {
            throw new RuntimeException("Dubbo Fescar Exception By Hobbit");
        }

        return account.update();
    }

}
