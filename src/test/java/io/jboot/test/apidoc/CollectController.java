package io.jboot.test.apidoc;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.apidoc.ApiRet;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/apidoc/ccc")
@Api(value = "CollectController",collect = {APIController.class,APIController2.class})
public class CollectController extends BaseController<ApiModel2> {

    @ApiOper("index")
    public List<String> index(List<String> list) {
        return null;
    }

    @ApiOper("users")
    public ApiRet<Page<ApiModel1>> users(Map<String, ApiModel1> map) {
        return null;
    }

    @ApiOper("index2")
    public String index2(long str) {
        return null;
    }


    @ApiOper("index3")
    public void index3() {
        return;
    }


}
