/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jwt;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/jwt")
public class JwtDemo extends JbootController {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.web.jwt.secret", "mySecret");
        Jboot.setBootArg("jboot.server.port", "8888");
        Jboot.run(args);
    }


    public void index() {
        setJwtAttr("key1", "test111");
        setJwtAttr("key2", "test");
        setJwtAttr("key3", "test");

        String token = createJwtToken();
        renderText(token);
    }

    public void show() {
        String value = getJwtPara("key1");
        renderText("jwt value : " + value);
    }


}
