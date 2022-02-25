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
package io.jboot.aop.annotation;

import java.lang.annotation.*;

/**
 * @author michael yang
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Transactional {

    /**
     * 使用哪个数据源
     *
     * @return
     */
    String config() default "";

    /**
     * 事务隔离级别
     *
     * @return
     */
    int transactionLevel() default -1;

    /**
     * return false 的时候，是否进行回滚
     *
     * @return
     */
    boolean rollbackForFalse() default false;

    /**
     * return ret.fail 的时候，是否进行回滚
     *
     * @return
     */
    boolean rollbackForRetFail() default false;


    /**
     * 返回 null 的时候，是否进行回滚
     *
     * @return
     */
    boolean rollbackForNull() default false;


    /**
     * 配置允许哪些异常不回滚
     *
     * @return
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    /**
     * 是否在新的线程里执行
     *
     * @return
     */
    boolean inNewThread() default false;

    /**
     * 使用哪个线程池来运行线程，需要在启动的时候，通过 TransactionalManager 来配置线程池及其名称
     *
     * @return
     */
    String threadPoolName() default "";

    /**
     * 是否以阻塞的方式运行线程，这个配置只有在返回值 void 情况下配置生效
     * 又返回的，默认都是阻塞运行线程
     *
     * @return
     */
    boolean threadWithBlocked() default false;

}