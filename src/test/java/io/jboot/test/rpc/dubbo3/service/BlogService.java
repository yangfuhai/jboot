package io.jboot.test.rpc.dubbo3.service;

import java.util.List;

public interface BlogService {
    String findById();
    List<String> findAll();
}
