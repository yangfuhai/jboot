package io.jboot.test.serializer;

public class TestObject {
    private String id;
    private int age;

    public TestObject() {
    }

    public TestObject(String id, int age) {
        this.id = id;
        this.age = age;
    }

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

    @Override
    public String toString() {
        return "TestObject{" +
                "id='" + id + '\'' +
                ", age=" + age +
                '}';
    }
}
