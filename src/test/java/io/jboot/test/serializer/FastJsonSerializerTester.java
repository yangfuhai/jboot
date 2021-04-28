package io.jboot.test.serializer;

import com.jfinal.captcha.Captcha;
import io.jboot.components.serializer.FastJsonSerializer;

public class FastJsonSerializerTester {

    public static void main(String[] args) {
        FastJsonSerializer fjs = new FastJsonSerializer();

        TestObject object = new TestObject("aaaa10001",18);
        byte[] bytes = fjs.serialize(object);

        TestObject dObj = (TestObject) fjs.deserialize(bytes);

        System.out.println(object);


        Captcha captcha = new Captcha("testKey","testValue",1000);
        byte[] cbytes = fjs.serialize(captcha);

        Captcha newCaptcha = (Captcha) fjs.deserialize(cbytes);

        System.out.println(captcha);
    }
}
