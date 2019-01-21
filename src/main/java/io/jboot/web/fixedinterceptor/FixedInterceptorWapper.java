/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.fixedinterceptor;

/**
 * FixedInterceptorWapper 排序用
 *
 * @author Rlax
 */
public class FixedInterceptorWapper {

    private FixedInterceptor fixedInterceptor;

    private int orderNo = 100;

    public FixedInterceptorWapper(FixedInterceptor fixedInterceptor) {
        this.fixedInterceptor = fixedInterceptor;
    }

    public FixedInterceptorWapper(FixedInterceptor fixedInterceptor, int orderNo) {
        this.fixedInterceptor = fixedInterceptor;
        this.orderNo = orderNo;
    }

    public FixedInterceptor getFixedInterceptor() {
        return fixedInterceptor;
    }

    public void setFixedInterceptor(FixedInterceptor fixedInterceptor) {
        this.fixedInterceptor = fixedInterceptor;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

}
