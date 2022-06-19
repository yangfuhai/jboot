package io.jboot.test.rpc.dubbo3.provider.service.impl;

import com.google.common.collect.Lists;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.test.rpc.dubbo3.service.BlogService;

import java.util.List;

@RPCBean
public class BlogServiceProvider implements BlogService {


    @Override
    public String findById() {
        return "id from provider";
    }

    @Override
    public List<String> findAll() {
        return Lists.newArrayList("item1","item2");
    }
}
