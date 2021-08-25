package io.jboot.test.rpc.commons;


import java.util.List;

public interface BookService {

    public String findById();
    public List<String> findAll();

    public void doOther();
}
