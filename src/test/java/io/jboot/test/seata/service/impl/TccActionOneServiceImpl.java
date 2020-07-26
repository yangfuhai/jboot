package io.jboot.test.seata.service.impl;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import io.jboot.aop.annotation.Bean;
import io.jboot.test.seata.account.IAccountService;
import io.jboot.test.seata.service.TccActionOneService;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * program: seata
 * description: ${description}
 * author: zxn
 * create: 2020-07-25 22:39
 **/
@Bean
public class TccActionOneServiceImpl implements TccActionOneService {

    @Inject
    private IAccountService accountService;

    @Override
    @TwoPhaseBusinessAction(name = "TccActionOne" , commitMethod = "commit", rollbackMethod = "rollback")
    public boolean prepare(BusinessActionContext actionContext, String account,int money, boolean flag) {
        System.out.println("actionContext获取Xid prepare>>> "+actionContext.getXid());
        System.out.println("actionContext获取TCC参数 prepare>>> "+actionContext.getActionContext("account"));
        accountService.updateStore(account, money);
       /* if (flag) {
            throw new RuntimeException("you have fail");
        }*/
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        System.out.println("actionContext获取TCC参数 commit >>> "+ actionContext.getActionContext("account") + ": " +
                actionContext.getActionContext("money"));
        String account = (String) actionContext.getActionContext("account");
        int money = (int) actionContext.getActionContext("money");
        accountService.update(account, money);
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        System.out.println("actionContext获取TCC参数 rollback >>> "+ actionContext.getActionContext("account") + ": " +
                actionContext.getActionContext("money"));
        String account = (String) actionContext.getActionContext("account");
        int money = (int) actionContext.getActionContext("money");
        accountService.updateRollbackStore(account, money);
        return true;
    }
}
