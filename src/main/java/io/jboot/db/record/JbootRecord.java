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
package io.jboot.db.record;

import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JbootRecord extends Record {


    //////
    @Override
    public BigInteger getBigInteger(String attr) {
        Object data = get(attr);
        if (data instanceof BigInteger) {
            return (BigInteger) data;
        }
        //数据类型 id(19 number)在 Oracle Jdbc 下对应的是 BigDecimal,
        //但是在 MySql 下对应的是 BigInteger，这会导致在 MySql 下生成的代码无法在 Oracle 数据库中使用
        //此处是为了解决这个问题的
        else if (data instanceof BigDecimal) {
            return ((BigDecimal) data).toBigInteger();
        } else if (data instanceof Number) {
            return BigInteger.valueOf(((Number) data).longValue());
        }
        //可能会抛出异常，应该让其抛出
        return (BigInteger) data;
    }


    @Override
    public BigDecimal getBigDecimal(String attr) {
        Object data = get(attr);
        if (data instanceof BigDecimal) {
            return (BigDecimal) data;
        } else if (data instanceof Number) {
            return new BigDecimal(data.toString());
        }
        //可能会抛出异常，应该让其抛出
        return (BigDecimal) data;
    }
}
