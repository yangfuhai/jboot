/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.test;

import com.jfinal.aop.Before;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/12
 */
@RequestMapping("/hello")
public class HelloWorld extends JbootController {

    @Before({MyInterceptor.class,HelloInterceptor.class})
    public void index(){
        renderText("hello world");
//        throw new NullPointerException();
    }

    public void ex(){
        throw new NullPointerException("log test");
    }


    @Before({HelloInterceptor.class})
    public void abc(String abc, long number){
        renderText("abc");
    }

    public static void main(String[] args){
        JbootApplication.run(args);
    }


    public static class MyInterceptor implements Interceptor {

        @Override
        public void intercept(Invocation inv) {
            System.out.println("MyInterceptor.intercept");
//            throw new NullPointerException("");
//            inv.getController().renderText("aaa");
            inv.invoke();
        }
    }


}