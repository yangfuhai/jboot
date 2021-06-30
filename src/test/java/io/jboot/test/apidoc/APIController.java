package io.jboot.test.apidoc;

import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/apidoc/test")
@Api("test")
public class APIController extends BaseController<ApiModel2> {

    @ApiOper("index")
    public List<String> index(){
        return null;
    }

    @ApiOper("users")
    public Map<String, ApiModel1> users(){
        return null;
    }


}
