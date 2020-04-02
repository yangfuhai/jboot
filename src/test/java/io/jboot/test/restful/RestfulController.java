package io.jboot.test.restful;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.NotAction;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import io.jboot.components.restful.HttpStatus;
import io.jboot.components.restful.ResponseEntity;
import io.jboot.components.restful.annotation.*;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.cors.EnableCORS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/restful")
public class RestfulController extends JbootController {

    public static class Data implements Serializable {
        private String id;
        private String name;
        private int age;

        public Data(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class RestfulInterceptor implements Interceptor {

        public void intercept(Invocation inv) {
            System.out.println("--------> restful request begin");
            inv.invoke();
            System.out.println("--------> restful request end");
        }
    }

    @Inject
    private RestfulService restfulService;

    @NotAction
    public List<Data> initData(){
        List<Data> users = new ArrayList<>();
        users.add(new RestfulController.Data("1", "tom", 18));
        users.add(new RestfulController.Data("2", "andy", 29));
        users.add(new RestfulController.Data("3", "max", 13));
        return users;
    }

    //GET /restful
    @GetMapping
    @EnableCORS
    @Before({RestfulInterceptor.class})
    @ResponseHeaders({@ResponseHeader(key = "d-head-1", value = "a"), @ResponseHeader(key = "d-head-2", value = "b")})
    public List<Data> users(){
        return initData();
    }

    // GET /restful/randomKey
    @GetMapping("/randomKey")
    public String randomKey(){
        return restfulService.getRandomKey();
    }

    // GET /restful/users
    @GetMapping("/users")
    public ResponseEntity<List<Data>> entityUsers(){
        return new ResponseEntity<>(initData()).addHeader("x-token", StrKit.getRandomUUID()).setHttpStatus(HttpStatus.ACCEPTED);
    }

    // PUT /restful
    @PutMapping
    public void create(@RequestBody RestfulController.Data data){
        System.out.println("get request body data:\n" + JsonKit.toJson(data));
    }

    // PUT /restful/createList
    @PutMapping("/createList")
    public void createUsers(@RequestBody List<Data> users){
        System.out.println("get request body data:\n" + JsonKit.toJson(users));
    }

    // DELETE /restful/:id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        System.out.println("delete by id : " + id );
    }

    // DELETE /restful/delete
    @DeleteMapping("/delete")
    public void deleteByName(@RequestParam(value = "name", required = true) String name,
                             @RequestHeader String token){
        System.out.println("delete by name : " + name);
        System.out.println("get token header : " + token);
    }
    
}
