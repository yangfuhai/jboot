package io.jboot.test.junit;

public class TestService{

    public String doSomething(){
        System.out.println(">>>>>>>>TestService.doSomething");
        return "ok";
    }

    public String doOther(){
        return "doOther";
    }
}
