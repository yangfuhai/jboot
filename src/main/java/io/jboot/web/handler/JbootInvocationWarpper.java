package io.jboot.web.handler;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;

import java.util.LinkedList;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class JbootInvocationWarpper extends Invocation {

    private Interceptor[] inters;
    private int index = 0;

    private static ThreadLocal<List<Interceptor>> invokedInterceptors = ThreadLocal.withInitial(LinkedList::new);

    public JbootInvocationWarpper(Action action, Controller controller) {
        super(action, controller);
        this.inters = action.getInterceptors();
    }

    @Override
    public void invoke() {
        if (index < inters.length) {
            invokedInterceptors.get().add(inters[index++]);
        }
        super.invoke();
    }

    public static List<Interceptor> getInvokedInterceptor() {
        return invokedInterceptors.get();
    }

    public static void clear(){
        invokedInterceptors.get().clear();
        invokedInterceptors.remove();
    }
}
