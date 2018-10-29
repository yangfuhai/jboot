package io.jboot.web.fixedinterceptor;

/**
 * FixedInterceptorWapper 排序用
 * @author Rlax
 *
 */
public class FixedInterceptorWapper {

    private FixedInterceptor fixedInterceptor;

    private int orderNo = 100;

    public FixedInterceptorWapper(FixedInterceptor fixedInterceptor) {
        this.fixedInterceptor = fixedInterceptor;
    }

    public FixedInterceptorWapper(FixedInterceptor fixedInterceptor, int orderNo) {
        this.fixedInterceptor = fixedInterceptor;
        this.orderNo = orderNo;
    }

    public FixedInterceptor getFixedInterceptor() {
        return fixedInterceptor;
    }

    public void setFixedInterceptor(FixedInterceptor fixedInterceptor) {
        this.fixedInterceptor = fixedInterceptor;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

}
