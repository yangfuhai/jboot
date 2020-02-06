/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.model;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/14
 */
public class Join {

    static final String TYPE_LEFT = " LEFT JOIN ";
    static final String TYPE_RIGHT = " RIGHT JOIN ";
    static final String TYPE_INNER = " INNER JOIN ";
    static final String TYPE_FULL = " FULL JOIN ";

    private String type;
    private String table;
    private String as;
    private String on;
    private boolean effective;

    public Join(String type,String table,boolean effective) {
        this.type = type;
        this.table = table;
        this.effective = effective;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }


    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public boolean isEffective() {
        return effective;
    }

    public void setEffective(boolean effective) {
        this.effective = effective;
    }
}
