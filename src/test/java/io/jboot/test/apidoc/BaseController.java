package io.jboot.test.apidoc;

import com.jfinal.core.Controller;
import io.jboot.apidoc.annotation.ApiOper;

import java.util.List;
import java.util.Map;

public class BaseController<T> extends Controller {

    @ApiOper("detail")
    public T detail(T t){
        return null;
    }


    @ApiOper("other")
    public Map<String, List<T>> other(Map<String, List<T>> data){
        return null;
    }
}
