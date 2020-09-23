package io.jboot.test.aop;

import com.jfinal.aop.Interceptor;
import io.jboot.aop.Interceptors;
import io.jboot.test.HelloInterceptor;

import java.util.Arrays;
import java.util.function.Predicate;

public class InterceptorsTest {

    public static void main(String[] args) {

        Interceptors interceptors = new Interceptors();

        interceptors.add(new NameInterceptor("name1"));
        interceptors.add(new NameInterceptor("name2"));
        interceptors.add(new NameInterceptor("name3"));
        interceptors.addToFirst(new NameInterceptor("name0"));
        interceptors.addToFirst(new NameInterceptor("name-1"));
        interceptors.add(new Name1Interceptor("Name1Interceptor"));
        interceptors.add(new NameInterceptor("name5"));
        interceptors.add(new NameInterceptor("name6"));
        interceptors.addBefore(new NameInterceptor("name1Before"),Name1Interceptor.class);
        System.out.println(interceptors.addAfter(new NameInterceptor("name1Afater"),Name1Interceptor.class));
        System.out.println(interceptors.addAfter(new NameInterceptor("HelloInterceptorAfater"), HelloInterceptor.class));
        interceptors.addAfter(new NameInterceptor("name1.5"), interceptor -> {
            if (interceptor instanceof NameInterceptor){
                NameInterceptor i = (NameInterceptor) interceptor;
                return i.getName().equals("name1");
            }
            return false;
        });


        interceptors.addBefore(new NameInterceptor("name2.5"), interceptor -> {
            if (interceptor instanceof NameInterceptor){
                NameInterceptor i = (NameInterceptor) interceptor;
                return i.getName().equals("name3");
            }
            return false;
        });

        System.out.println(Arrays.toString(interceptors.toArray()));
    }
}
