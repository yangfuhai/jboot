package io.jboot.test.aop;

import com.jfinal.core.Controller;
import io.jboot.aop.ValueFilter;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.aop.annotation.FilterBy;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/aop/filter")
public class FilterController extends Controller {

    public void test1(@FilterBy(Filter1.class) String orderBy) {
        renderText(orderBy);
    }

    public void test2(@FilterBy({Filter1.class, Filter2.class}) String orderBy) {
        renderText(orderBy);
    }


    public void test3(@FilterBy({Filter3.class}) String orderBy) {
        renderText(orderBy);
    }

    public void test4(@FilterBy({Filter3.class}) @DefaultValue("id desc") String orderBy) {
        renderText(orderBy);
    }


    public static class Filter1 implements ValueFilter {

        @Override
        public Object doFilter(Object orignal) {
            return orignal + "filter1";
        }
    }

    public static class Filter2 implements ValueFilter {

        @Override
        public Object doFilter(Object orignal) {
            return orignal + "filter2";
        }
    }


    public static class Filter3 implements ValueFilter {

        @Override
        public Object doFilter(Object orignal) {
            return null;
        }
    }
}
