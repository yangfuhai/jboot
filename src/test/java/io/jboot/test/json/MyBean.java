package io.jboot.test.json;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class MyBean {
//    private String id;
    private int age;
    private BigInteger amount;

    @NotNull
    public String getId() {
        return null;
    }


//    public void setId(String id) {
//        this.id = id;
//    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "MyBean{" +
//                "id='" + id + '\'' +
                ", age=" + age +
                ", amount=" + amount +
                '}';
    }
}
