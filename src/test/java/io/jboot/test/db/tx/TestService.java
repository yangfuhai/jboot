package io.jboot.test.db.tx;

import com.jfinal.kit.Ret;
import io.jboot.aop.annotation.Transactional;

public class TestService {

    @Transactional
    public void test1() {
        System.out.println("currentThread------->>>>" + Thread.currentThread().getName());
        System.out.println("test1");
    }

    @Transactional(rollbackForFalse = true)
    public boolean test2() {
        System.out.println("currentThread------->>>>" + Thread.currentThread().getName());
        System.out.println("test1");
        return false;
    }

    @Transactional(inNewThread = true,threadPoolName = "aaa")
    public Ret test3() {
        System.out.println("currentThread------->>>>" + Thread.currentThread().getName());
        return Ret.ok();
    }
}
