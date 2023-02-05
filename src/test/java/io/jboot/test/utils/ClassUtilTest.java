package io.jboot.test.utils;

import org.junit.Test;

public class ClassUtilTest {


    public static class TestObject{
        private String a;
        private String b;

        public TestObject() {
        }

        public TestObject(String a) {
            this.a = a;
        }

        public TestObject(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "a='" + a + '\'' +
                    ", b='" + b + '\'' +
                    '}';
        }
    }


    @Test
    public void testNewInstance(){
//        TestObject testObject0 = ClassUtil.newInstance(TestObject.class);
//        System.out.println(testObject0);
//
//        TestObject testObject1 = ClassUtil.newInstance(TestObject.class,"a");
//        System.out.println(testObject1);
//
//        TestObject testObject2 = ClassUtil.newInstance(TestObject.class,null);
//        System.out.println(testObject2);
//
//        TestObject testObject3 = ClassUtil.newInstance(TestObject.class,"aa","bbb");
//        System.out.println(testObject3);
//
//        TestObject testObject4 = ClassUtil.newInstance(TestObject.class,null,"bbb");
//        System.out.println(testObject4);
//
//        TestObject testObject5 = ClassUtil.newInstance(TestObject.class,null,"bbb","ccc");
//        System.out.println(testObject5);
    }



}
