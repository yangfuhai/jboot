package io.jboot.test.rpc.commons;

import com.google.common.collect.Lists;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.List;

@RPCBean
public class BlogServiceMock implements BlogService {

    @Override
    public String findById() {
        System.err.println("BlogServiceMock.findById() invoked.");
        return "id from BlogServiceMock";
    }

    @Override
    public List<String> findAll() {
        return Lists.newArrayList("item1","item2");
    }
}
