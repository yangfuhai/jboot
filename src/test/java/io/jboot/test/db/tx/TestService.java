package io.jboot.test.db.tx;

import com.jfinal.kit.Ret;
import io.jboot.db.tx.TxEnable;

public class TestService {

    @TxEnable
    public void test1() {
        System.out.println("test1");
    }

    @TxEnable
    public boolean test2() {
        System.out.println("test1");
        return false;
    }

    @TxEnable(inNewThread = true)
    public Ret test3() {
        return Ret.ok();
    }
}
