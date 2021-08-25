package io.jboot.test.rpc.commons;

import com.google.common.collect.Lists;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.List;

@RPCBean
public class BookServiceProvider implements BookService {

    @Override
    public String findById() {
        System.err.println("BookServiceProvider.findById() invoked.");
        return "id from BookServiceProvider";
    }

    @Override
    public List<String> findAll() {
        return Lists.newArrayList("item1","item2");
    }

    @Override
    public void doOther() {

    }
}
