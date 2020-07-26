package io.jboot.test.seata.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;

/**
 * program: seata
 * description: ${description}
 * author: zxn
 * create: 2020-07-25 22:29
 **/
public interface TccActionOneService {

    /**
     * Prepare boolean.
     *
     * @param actionContext the action context
     * @param account
     * @return the boolean
     */

    public boolean prepare(BusinessActionContext actionContext, @BusinessActionContextParameter(paramName = "account") String account, @BusinessActionContextParameter(paramName = "money") int money,
                            @BusinessActionContextParameter(paramName = "flag") boolean flag);

    /**
     * Commit boolean.
     *
     * @param actionContext the action context
     * @return the boolean
     */
    public boolean commit(BusinessActionContext actionContext);

    /**
     * Rollback boolean.
     *
     * @param actionContext the action context
     * @return the boolean
     */
    public boolean rollback(BusinessActionContext actionContext);
}
