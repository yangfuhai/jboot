/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dubborestful;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("users")
public class UserServiceImpl implements UserService {


    @Override
    @GET
    @Path("hello")
    @Consumes({MediaType.TEXT_PLAIN})
    public String test(String name) {

        System.out.println("UserServiceImpl test() invoked!!!");

        return "test";
    }

    @Override
    @GET
    @Path("get")
    @Consumes({MediaType.TEXT_PLAIN})
    public String get() {

        System.out.println("UserServiceImpl get() invoked!!!");

        return "hello , dubbo restful";
    }




}
