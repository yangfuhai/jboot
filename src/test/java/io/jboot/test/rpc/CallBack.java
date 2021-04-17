package io.jboot.test.rpc;

public class CallBack {

    public void oninvoke(){
        System.out.println("CallBack.oninvoke...");


    }

    public void onthrow(Throwable ex) {
//            errors.put(id, ex);
        System.out.println(">>>>>onthrow>>>>");
    }
}
