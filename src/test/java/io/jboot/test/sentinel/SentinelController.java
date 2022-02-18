/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.test.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/3
 *
 * 使用方法：
 * http://jbootprojects.gitee.io/docs/docs/sentinel.html
 */
@RequestMapping("/sentinel")
public class SentinelController extends JbootController {

    @SentinelResource(blockHandler = "block")
    public void index(){
        renderText("sentinel index...");
    }


    /**
     * 注意：这个降级方法里的参数，必须是 SentinelResource 方法 index() 参数最后多出一个 BlockException 参数
     * @param ex
     */
    public void block(BlockException ex){
        renderText("sentinel block");
    }
}
