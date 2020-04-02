package io.jboot.components.restful;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.web.fixedinterceptor.FixedInvocation;

public class RestfulInvocation extends FixedInvocation {

    public RestfulInvocation(Invocation invocation) {
        super(invocation);
    }

    @Override
    public Controller getController() {
        if (getTarget() == null) {
            throw new RuntimeException("This method can only be used for action interception");
        }
        return (Controller)getTarget();
    }
}
