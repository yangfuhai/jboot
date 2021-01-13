package io.jboot.test.seata.account;


import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.service.JbootServiceBase;
import io.jboot.test.seata.commons.Account;

@RPCBean
@Bean
public class AccountServiceProvider extends JbootServiceBase<Account> implements IAccountService {
    private static Account dao = new Account();

    public boolean deposit(Integer accountId, Integer money) {

        Account account = dao.findById(accountId);
        account.set("money", account.getInt("money") + money);

        if (money > 1000) {
            throw new RuntimeException(AccountServiceProvider.class.getSimpleName()+"Dubbo Seata Exception By Hobbit ");
        }

        return account.saveOrUpdate();
    }

    @Override
    public boolean updateStore(String account, Integer money) {
        Account account1 = dao.findFirst("select * from seata_account where account = ? ", account);
        account1.set("store", account1.getInt("store") + money);
        return account1.saveOrUpdate();
    }

    @Override
    public boolean updateRollbackStore(String account, Integer money) {
        Account account1 = dao.findFirst("select * from seata_account where account = ? ", account);
        account1.set("store", account1.getInt("store") - money);
        return account1.saveOrUpdate();
    }

    @Override
    public boolean update(String account, Integer money) {
        Account account1 = dao.findFirst("select * from seata_account where account = ? ", account);
        account1.set("store", account1.getInt("store") - money);
        account1.set("money",  account1.getInt("money") + money);
        return account1.saveOrUpdate();
    }
}
