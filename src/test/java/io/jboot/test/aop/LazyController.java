package io.jboot.test.aop;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import io.jboot.aop.annotation.Lazy;

@Path("lazy")
public class LazyController extends Controller {

//    @Inject
//    private LayzService1 userService;

    @Inject
    @Lazy
    LayzService2 bService;

    public void index1() {
        System.out.println("");
//        System.out.println("userService ---->" + userService);
        System.out.println("bService ---->" + bService);

        renderText("index1");
    }

    public void index2() {

        bService.doSth();

//        System.out.println("userService ---->" + userService);
//        System.out.println("bService ---->" + bService);


        renderText("index2");
    }
}
