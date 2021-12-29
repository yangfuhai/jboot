package io.jboot.test.utils;

public class ReflectBean {

    private static String someStaticValue = "staticvalue";
    private String id;
    private int age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private void doSomeThing(){
        System.out.println("doSomeThing invoked!!");
    }

    @Override
    public String toString() {
        return "ReflectBean{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", someStaticValue=" + someStaticValue +
                '}';
    }
}
