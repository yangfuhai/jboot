package io.jboot.test.rpc.commons;

import com.google.common.collect.Lists;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.List;

@RPCBean
public class BlogServiceProvider implements BlogService {

    @Override
    public String findById() {
        System.out.println("BlogServiceProvider.findById() invoked.");
        return "id from provider";
    }

    @Override
    public List<String> findAll() {
        return Lists.newArrayList("item1","item2");
    }
}
